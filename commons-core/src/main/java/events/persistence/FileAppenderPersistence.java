package events.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import wheel.io.files.Directory;
import core.events.eventslist.EventTransaction;
import events.PersistenceManager;

public class FileAppenderPersistence implements PersistenceManager {

	private final Directory directory;
	private final Serializer serializer;

	List<EventTransaction> eventsFromFile = null;

	public FileAppenderPersistence(final Directory directory, Serializer serializer) {
		this.directory = directory;
		this.serializer = serializer;
	}

	@Override
	public void writeEvent(EventTransaction event) {
		
		OutputStream out = directory.openFileForAppendOrCry(serializer.fileName());	
		
		try {			
			writeObjectOrCry(event, out);
			if(eventsFromFile != null)
				eventsFromFile.add(event);
		} finally {
			closeOrCry(out);
		}
	}

	@Override
	public List<EventTransaction> getEventsFromFile() {
		boolean dataFileStillDoesNotExist = !directory.fileExists(serializer.fileName());
		if (dataFileStillDoesNotExist)
			return new ArrayList<EventTransaction>();
		
		InputStream in = openInStreamOrCry();

		try{
			return readEvents(in);			
		}  finally {
			closeOrCry(in);
		}
	}

	@Override
	public List<EventTransaction> getEventTransactions() {
		if (eventsFromFile == null){
			eventsFromFile = getEventsFromFile();
		}
		return eventsFromFile;
	}

	protected List<EventTransaction> readEvents(InputStream in) {
		List<EventTransaction> list = new ArrayList<EventTransaction>();
		
		Collection<EventTransaction> event = null;
		while ((event = readEventsOrCry(in))!= null)
			list.addAll(event);
		return list;
	}

	@SuppressWarnings({ "unchecked" })
	private Collection<EventTransaction> readEventsOrCry(InputStream in) {
		
		Object readObject = readObjectOrCry(in);

		if (readObject == null)
			return null;
		
		if (readObject instanceof List)
			return (Collection<EventTransaction>) readObject;
		
		return Arrays.asList((EventTransaction) readObject);
	}

	private Object readObjectOrCry(InputStream in) {
		return serializer.readObjectOrCry(in);
	}

	private InputStream openInStreamOrCry() {
		InputStream in;
		try {
			in = directory.openFile(serializer.fileName());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return in;
	}

	private void closeOrCry(java.io.Closeable out) {
		try {
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	private void writeObjectOrCry(EventTransaction event, OutputStream out) {
		serializer.writeObjectOrCry(event, out);
	}
}
