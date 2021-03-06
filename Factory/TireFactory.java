import java.awt.Point;
import java.util.concurrent.BrokenBarrierException;

public class TireFactory extends Thread {
	Point position; // Posição da fábrica
	Tire [] inventory; // Estoque da fábrica
	
	/**
	 * Cria uma nova fábrica
	 */
	public TireFactory()
	{
		this.position = Positions.getTireFactoryPosition();
		this.inventory = new Tire[Constants.inventoryLength];
		for(int i = 0; i < inventory.length; i++)
			inventory[i] = null;
	}
	
	/**
	 * Faz um pedido de um tipo específico de pneu
	 * @param type tipo da pneu
	 */
	public synchronized void newRequest(int type)
	{
		for(int i = 0; i < inventory.length; i++)
			if(inventory[i] == null) inventory[i] = new Tire(type, this.getInventoryPositon(i));
	}
	
	/**
	 * Se a fábrica tem, no inventário um dado tipo da pneu
	 * @param type Tipo de pneu
	 * @return Retorna true se tem a pneu ou false caso contrário
	 */
	public synchronized boolean hasATire(int type)
	{
		for(int i = 0; i < inventory.length; i++)
			if(inventory[i] != null) 
				if(inventory[i].getType() == type && inventory[i].isReady()) return true;
		return false;
	}
	
	/**
	 * Retira um pneu da fábrica (retorna null se não tiver pneu daquele tipo)
	 * @param type Tipo do pneu desejado
	 * @return Pneu do tipo desejado, se existente; null caso contrário
	 */
	public synchronized Tire takeProduct(int type)
	{
		for(int i = 0; i < inventory.length; i++)
			if(inventory[i] != null) 
				if(inventory[i].getType() == type && inventory[i].isReady())
				{
					Tire tire = inventory[i];
					inventory[i] = null;
					return tire;
				}
		return null;
	}
	
	/**
	 * Retorna a posição no cenário de um dado local do inventário
	 * @param i posição no inventário
	 * @return Retorna a posição no cenário de um dado local do inventário
	 */
	Point getInventoryPositon(int i)
	{
		return new Point(this.position.x + 2*(i%2), this.position.y + ((int)i/2));
	}
	
	/**
	 * Atualiza peças no inventário
	 */
	private void Update()
	{
		synchronized (this)
		{
			for(int i = 0; i < inventory.length; i++)
				if(inventory[i] != null) inventory[i].Update();
		}
		
		try {
			Constants.barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Imprime peças no inventário
	 * @param draw Imprime peças no inventário
	 */
	public void Draw(Draw draw)
	{
		for(int i = 0; i < inventory.length; i++)
			if(inventory[i] != null) inventory[i].Draw(draw);
	}
	
	public void run()
	{
		while (true)
			Update();
	}
}
