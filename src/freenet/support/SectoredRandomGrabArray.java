package freenet.support;

import java.util.HashMap;

import freenet.crypt.RandomSource;

/**
 * Like RandomGrabArray, but there is an equal chance of any given client's requests being
 * returned.
 */
public class SectoredRandomGrabArray implements RemoveRandom {

	private final HashMap grabArraysByClient;
	private RemoveRandomWithClient[] grabArrays;
	private final RandomSource rand;
	
	public SectoredRandomGrabArray(RandomSource rand) {
		this.rand = rand;
		this.grabArraysByClient = new HashMap();
		grabArrays = new RemoveRandomWithClient[0];
	}

	/**
	 * Add directly to a RandomGrabArrayWithClient under us. */
	public synchronized void add(Object client, RandomGrabArrayItem item) {
		RandomGrabArrayWithClient rga;
		if(!grabArraysByClient.containsKey(client)) {
			rga = new RandomGrabArrayWithClient(client, rand);
			RemoveRandomWithClient[] newArrays = new RemoveRandomWithClient[grabArrays.length+1];
			System.arraycopy(grabArrays, 0, newArrays, 0, grabArrays.length);
			newArrays[grabArrays.length] = rga;
			grabArrays = newArrays;
			grabArraysByClient.put(client, rga);
		} else {
			rga = (RandomGrabArrayWithClient) grabArraysByClient.get(client);
		}
		rga.add(item);
	}

	/**
	 * Get a grabber. This lets us use things other than RandomGrabArrayWithClient's, so don't mix calls
	 * to add() with calls to getGrabber/addGrabber!
	 */
	public synchronized RemoveRandomWithClient getGrabber(Object client) {
		return (RemoveRandomWithClient) grabArraysByClient.get(client);
	}

	/**
	 * Put a grabber. This lets us use things other than RandomGrabArrayWithClient's, so don't mix calls
	 * to add() with calls to getGrabber/addGrabber!
	 */
	public synchronized void addGrabber(Object client, RemoveRandomWithClient requestGrabber) {
		grabArraysByClient.put(client, requestGrabber);
		RemoveRandomWithClient[] newArrays = new RemoveRandomWithClient[grabArrays.length+1];
		System.arraycopy(grabArrays, 0, newArrays, 0, grabArrays.length);
		newArrays[grabArrays.length] = requestGrabber;
		grabArrays = newArrays;
	}

	public synchronized RandomGrabArrayItem removeRandom() {
		while(true) {
			if(grabArrays.length == 0) return null;
			int x = rand.nextInt(grabArrays.length);
			RemoveRandomWithClient rga = grabArrays[x];
			if(Logger.shouldLog(Logger.MINOR, this))
				Logger.minor(this, "Picked "+x+" of "+grabArrays.length+" : "+rga+" : "+rga.getClient());
			RandomGrabArrayItem item = rga.removeRandom();
			if(rga.isEmpty() || (item == null)) {
				Object client = rga.getClient();
				grabArraysByClient.remove(client);
				RemoveRandomWithClient[] newArray = new RemoveRandomWithClient[grabArrays.length-1];
				if(x > 0)
					System.arraycopy(grabArrays, 0, newArray, 0, x);
				if(x < grabArrays.length-1)
					System.arraycopy(grabArrays, x+1, newArray, x, grabArrays.length - (x+1));
				grabArrays = newArray;
			}
			if(item == null) continue;
			if(item.isCancelled()) continue;
			return item;
		}
	}

	public synchronized boolean isEmpty() {
		return grabArrays.length == 0;
	}
	
}
