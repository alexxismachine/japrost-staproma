package de.japrost.staproma.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.japrost.staproma.TaskState;

/**
 * This is a simple base implementation of a Task.
 *
 * @author alexxismachine (Ulrich David)
 */
public abstract class AbstactTask implements Task {

	/**
	 * The sub tasks of this task.
	 */
	// FIXME make own class
	protected Collection<Task> subTasks = new ArrayList<>();
	/**
	 * The state of this task.
	 */
	protected TaskState state;
	private final String description;
	private final Task parent;
	private final List<String> content = new ArrayList<>();
	private short priority;

	/**
	 * Create a task with the given parameter.
	 *
	 * @param parent the parent task.
	 * @param description the description.
	 */
	protected AbstactTask(final Task parent, final String description) {
		this.parent = parent;
		this.description = description;
	}

	/**
	 * Set the state of the Task
	 *
	 * @param state the new state.
	 */
	// FIXME make state unmodifiable?
	public void setState(final TaskState state) {
		this.state = state;
	}

	@Override
	public boolean isInState(final TaskState status) {
		// hier nur eine dummy implementierung die true zurück gibt. alle
		// anderen mit eigener!
		String enter = "-> state " + getDescription() + ": " + state;
		String leave = "<- state " + getDescription() + ": ";
		System.out.println(enter);
		if (status == null) {
			System.out.println(leave + "TRUE on null");
			return true;
		}
		if (status.equals(state)) {
			System.out.println(leave + "TRUE on equals");
			return true;
		}
		for (Task subTask : subTasks) {
			if (subTask.isInState(status)) {
				System.out.println(leave + "TRUE on sub");
				return true;
			}
		}
		System.out.println(leave + "FALSE fallback");
		return false;
	}

	@Override
	public Iterator<Task> iterator() {
		return subTasks.iterator();
	}

	@Override
	public boolean hasChildren() {
		return !subTasks.isEmpty();
	}

	@Override
	public void addChild(final Task task) {
		subTasks.add(task);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Task getParent() {
		return parent;
	}

	@Override
	public List<String> getContent() {
		return content;
	}

	@Override
	public void addContent(final String line) {
		this.content.add(line);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getPriority() {
		return priority;
	}

	/**
	 * Set the priority.
	 *
	 * @param priority the new priority.
	 */
	public void setPriority(final short priority) {
		this.priority = priority;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>This implementation</strong> returns the own and all sub task priorities.
	 */
	@Override
	public List<Short> priorities() {
		if (subTasks.isEmpty()) {
			if (priority == 0) {
				return Collections.emptyList();
			}
			return Collections.singletonList(priority);
		}
		Set<Short> collect = subTasks.stream().map(s -> s.priorities()).flatMap(List::stream)
				.filter(s -> s.shortValue() != 0)
				.collect(Collectors.toSet());
		if (priority != 0) {
			collect.add(priority);
		}
		return new ArrayList<>(collect);
	}

}
