package com.github.aureliano.defero.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

import com.github.aureliano.defero.config.input.InputFileConfig;
import com.github.aureliano.defero.exception.DeferoException;
import com.github.aureliano.defero.filter.DefaultEmptyFilter;

public class FileTailerDataReader extends AbstractDataReader {

	private InputFileConfig _inputConfiguration;
	
	private RandomAccessFile randomAccessFile;
	private long fileLength;
	private long filePointer;
	private long initialTimeMillis;
	
	private static final Logger logger = Logger.getLogger(FileTailerDataReader.class.getName());	
	
	public FileTailerDataReader() {
		super();
		this.fileLength = this.filePointer = this.initialTimeMillis = 0;
	}

	@Override
	public Object nextData() {
		this.initialize();
		Object data = this.readNextData();
		if (data != null) {
			return data;
		}
		
		while (this.shouldExecute()) {
			long currentFileLength = this.currentFileLength();
			while (this.fileLength == currentFileLength && this.shouldExecute()) {
				try {
					Thread.sleep(this._inputConfiguration.getTailDelay());
					currentFileLength = this.currentFileLength();
				} catch (InterruptedException ex) {
					throw new DeferoException(ex);
				}
			}
			
			while (this.fileLength != currentFileLength) {
				if (this.fileLength > currentFileLength) {
					this.rotateFile();
				} else if (this.fileLength < currentFileLength) {
					this.initializeRandomAccessFile();
					return null;
				}
			}
		}
		
		super.markedToStop = true;
		return null;
	}

	private Object readNextData() {
		String line = null;
		Object data = null;
		boolean accepted = false;
		
		try {
			line = this.readNextLine();
			if (line == null) {
				return null;
			}
			
			do {
				super.executeBeforeReadingMethodListeners();
				
				data = super.parser.parse(super.prepareLogEvent(line));
				if (data == null) {
					continue;
				}
				accepted = super.filter.accept(data);
				
				super.executeAfterReadingMethodListeners(data, accepted);				
			} while (!accepted && (line = this.readNextLine()) != null);
			
			this.filePointer = this.randomAccessFile.getFilePointer() + 1;
			
			return (accepted) ? data : null;
		} catch (IOException ex) {
			throw new DeferoException(ex);
		}
	}

	private void rotateFile() {
		logger.info("File got smaller! Maybe file rotation? Reseting and reading from beginning.");
		this.fileLength = this.currentFileLength();
		this.filePointer = super.lineCounter = 0;
	}

	@Override
	public void endResources() {
		logger.info(" >>> Flushing and closing stream reader.");
		if (this.randomAccessFile == null) {
			return;
		}
		
		try {
			this.randomAccessFile.close();
			this.randomAccessFile = null;
		} catch (IOException ex) {
			throw new DeferoException(ex);
		}
	}
	
	private boolean shouldExecute() {
		long diff = System.currentTimeMillis() - this.initialTimeMillis;
		return (diff < this._inputConfiguration.getTailInterval());
	}
	
	private long currentFileLength() {
		return new File(this._inputConfiguration.getFile().getPath()).length();
	}
	
	private void initialize() {
		if (this.initialTimeMillis > 0) {
			return;
		}
		
		this.initialTimeMillis = System.currentTimeMillis();
		
		if (super.filter == null) {
			super.filter = new DefaultEmptyFilter();
		}
		
		this.initializeRandomAccessFile();
		this._inputConfiguration = (InputFileConfig) super.inputConfiguration;
		
		logger.info("Reading data from " + this._inputConfiguration.getFile().getPath());
		logger.info("Delay milliseconds: " + this._inputConfiguration.getTailDelay());
		logger.info("Tail about " + this._inputConfiguration.getTailInterval() + " milliseconds.");
		logger.info("Data encondig: " + this._inputConfiguration.getEncoding());
	}
	
	private void initializeRandomAccessFile() {
		try {
			this.randomAccessFile = new RandomAccessFile(this._inputConfiguration.getFile(), "r");
			this.fileLength = this._inputConfiguration.getFile().length();
			this.randomAccessFile.seek(this.filePointer);
			
			this._inputConfiguration.withFile(new File(this._inputConfiguration.getFile().getPath()));
		} catch (IOException ex) {
			throw new DeferoException(ex);
		}
	}
	
	protected String readNextLine() {		
		try {
			String line = null;
			if (super.unprocessedLine != null) {
				line = super.unprocessedLine;
				super.unprocessedLine = null;
			} else {
				line = this.randomAccessFile.readLine();
				if (line != null) {
					line = new String(line.getBytes(), this._inputConfiguration.getEncoding());
					super.lineCounter++;
				}
			}
			
			return line;
		} catch (IOException ex) {
			throw new DeferoException(ex);
		}
	}
}