package net.chlab.emailsender.ejb;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class SequenceCounter {

	private AtomicLong sequencer;
	
	@PostConstruct
	public void init() {
		sequencer = new AtomicLong();
	}
	
	public long nextValue() {
		return sequencer.incrementAndGet();
	}
	
}
