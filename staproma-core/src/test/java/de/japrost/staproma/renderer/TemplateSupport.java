package de.japrost.staproma.renderer;

import org.junit.jupiter.api.Test;

import de.japrost.staproma.task.AnonymousTask;
import de.japrost.staproma.task.FolderTask;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;

class TemplateSupport {
	@Test
	void doStuff() {
		// the base container that will not be rendered
		var root = new FolderTask(null, "ROOT");
		// in most cases the root container does only contain other containers so lets
		// add some
		var c001 = addFolderContainer(root, "c001");
		var c002 = addFolderContainer(root, "c002");
		var c003 = addFolderContainer(root, "c003");
		var c004 = addFolderContainer(root, "c004");
		// c001 just contains Leafs so all should be rendered in one ul
		addLeaf(c001, "ul before l001");
		addLeaf(c001, "l002");
		addLeaf(c001, "l003");
		addLeaf(c001, "/ul after l004");
		// c002 starts with a Leaf and continues with a container
		addLeaf(c002, "ul around l005");
		var c002_1 = addFolderContainer(c002, "c002-1");
		addLeaf(c002_1, "ul around l006");
		// c003 mixes leafs and containers
		addLeaf(c003, "l007");
		var c003_1 = addFolderContainer(c003, "c003-1");
		addLeaf(c003_1, "l008");
		addLeaf(c003, "l009");
		
		// leaf on root
		addLeaf(root, "leaf on root");

		doRender(root);

	}

	private void doRender(Task root) {
		walkSubTree(root, 0);
	}

	private void walkSubTree(Task task, int level) {
		// System.err.println(" " + task.getDescription());
		// TODO state check
		if (level > 0) {
			printContainerTask(task, level);
			processChildren(task, level);
		} else {
			// no output of root
			processChildren(task, level);
		}
	}

	private void processChildren(Task task, int level) {
		boolean ul = false;
		for (Task subTask : task) {
			if (!subTask.isContainer()) {
				if (!ul) {
					System.out.println("  ".repeat(level) + "<ul>");
					ul = true;
				}
				printLeafTask(subTask, level);
				continue;
			}
			if (ul) {
				System.out.println("  ".repeat(level) + "</ul1>");
				ul = false;
			}
			walkSubTree(subTask, level + 1);
		}
		if (ul) {
			System.out.println("  ".repeat(level) + "</ul2>");
		}
	}

	private void printContainerTask(Task task, int level) {
		System.out.println(" ".repeat(level) + "h" + level + ": " + task.getDescription());

	}

	private void printLeafTask(Task task, int level) {
		System.out.println("   ".repeat(level) + task.getDescription());

	}

	private Task addFolderContainer(Task parent, String name) {
		var container = new FolderTask(parent, name + " container");
		parent.addChild(container);
		return container;
	}

	private Task addLeaf(Task parent, String name) {
		var container = new LeafTask(parent, name + " leaf in " + parent.getDescription());
		parent.addChild(container);
		return container;
	}

	private Task addAnonymounsContainer(Task parent) {
		var container = new AnonymousTask(parent);
		parent.addChild(container);
		return container;
	}
}
