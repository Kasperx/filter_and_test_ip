package testips;

public class ThreadConsole extends Thread{
	
	Main obj;
	
	public ThreadConsole(Main obj)
	{
		super();
		this.obj = obj;
	}

//	public void run(Main main) {
//		try {
//			obj = main;
//			while(true)
//			{
//				this.sleep((long) 5000);
//				obj.consoleLog(obj.getObject());
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void run()
	{
		try {
			while(true)
			{
				this.sleep((long) 5000);
				obj.showNow = true;
				Main.consoleLog();
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
		return;
	}
}
