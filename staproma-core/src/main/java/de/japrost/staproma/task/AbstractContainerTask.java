package de.japrost.staproma.task;

public abstract class AbstractContainerTask extends AbstactTask {

	protected AbstractContainerTask(Task parent, String description) {
		super(parent, description);
	}

	@Override
	public boolean isContainer() {
		return true;
	}

}
