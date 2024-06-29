package com.falling.commands;

import com.falling.events.Event;
import com.falling.events.EventQueue;

/**
 * SendEventCmd a helper class for running events in buttons that operate on Commands
 */
public class SendEventCmd extends Command {

    private Event event;

	public SendEventCmd(Event event) {
		super(null);
        this.event = event;
	}

	@Override
	public void execute() {
        EventQueue.instance.sendEvent(event);
	}

    public SendEventCmd setEvent(Event event) {
        this.event = event;
        return this;
    }
}
