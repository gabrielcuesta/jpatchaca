//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package wheel.io.network.mocks;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import wheel.io.network.ObjectSocket;
import wheel.io.serialization.DeepCopier;
import wheel.lang.Threads;


public class ObjectSocketMock implements ObjectSocket {

	private ObjectSocketMock _counterpart;
	private final List<Object> _receivedObjects = new LinkedList<Object>();
	private Permit _permit;

	ObjectSocketMock(Permit permit) {
		initialize(permit, new ObjectSocketMock(this, permit));
	}

	private ObjectSocketMock(ObjectSocketMock counterpart, Permit permit) {
		initialize(permit, counterpart);
	}

	private void initialize(Permit permit, ObjectSocketMock counterpart) {
		_permit = permit;
		_permit.addObjectToNotify(this);
		_counterpart = counterpart;
	}

	@Override
	public void writeObject(Object object) throws IOException {
		_permit.check();
		_counterpart.receive(DeepCopier.deepCopy(object));
	}

	private synchronized void receive(Object object) {
		_receivedObjects.add(object);
		notify();
	}

	@Override
	public synchronized Object readObject() throws IOException {
		_permit.check();
		if (_receivedObjects.isEmpty()) Threads.waitWithoutInterruptions(this);
		_permit.check();
		return _receivedObjects.remove(0);
	}

	@Override
	public void close() {
		//Implement.
	}

	public ObjectSocket counterpart() {
		return _counterpart;
	}
	
}
