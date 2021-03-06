import java.awt.Point;
import java.util.concurrent.BrokenBarrierException;

public class Scheduler extends Thread {
	String[] image;
	Point position;
	WorkSite[] workSites;
	PendingRequests pendingRequests;
	EngineFactory engineFactory; 
	BodyFactory bodyFactory; 
	TireFactory tireFactory;
	Worker[] workers;
	
	/**
	 * Cria novo scheduler
	 * @param pendingRequests Lista de espera de pedidos
	 * @param engineFactory Fábrica de motores
	 * @param bodyFactory Fábrica de carcaças
	 * @param tireFactory Fábrica de pneus
	 * @param workers Vetor de trabalhadores
	 */
	public Scheduler(PendingRequests pendingRequests, EngineFactory engineFactory, BodyFactory bodyFactory, TireFactory tireFactory, WorkSite[] workSites, Worker[] workers)
	{
		this.image = Images.getScheduler();
		this.position = Positions.getSchedulerPosition();
		this.pendingRequests = pendingRequests;
		
		this.engineFactory = engineFactory;
		this.bodyFactory = bodyFactory;
		this.tireFactory = tireFactory;
		this.workers = workers;
		this.workSites = workSites;
	}
	
	/**
	 * Procura por funcionário livre e entrega o próximo pedido da fila de espera para ele
	 */
	private void Update()
	{
		/* Procura por um funcionário livre */
		Worker worker = null;
		for(int i = 0; i < workers.length; i++)
		{
			if(workers[i].isFree())
			{
				worker = workers[i];
				break;
			}
		}
		
		/* Entrega próximo trabalho para ele */
		if(worker != null) // existe um funcionário livre
		{
			Request request = pendingRequests.peekRequest(0);
			if(request != null) worker.receiveRequest(pendingRequests.getRequest(request.getID())); // Entrega o próximo para ele
		}
		
		try {
			Constants.barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Imprime scheduler e workSites
	 * @param draw Local de impressão
	 */
	public void Draw(Draw draw)
	{
		draw.addPrint(image, position);
		if(workSites == null) 
			{System.exit(10);System.out.println("\\o/");}
		for(int i = 0; i < workSites.length; i++){
			workSites[i].Draw(draw);}
	}
	
	public void run()
	{
		while (true)
			Update();
	}
}
