package parallelTree;

import java.util.concurrent.BrokenBarrierException;

public class VerifyTree implements Runnable {
	TreeNode tree;

	public VerifyTree(TreeNode tree) {
		this.tree = tree;
	}

	public boolean isCorrect(TreeNode tree) {
		if (tree == null) {
			return true;
		}

		if (tree.name >= 64) {
			return true;
		}

		if (tree.left == null) {
			return false;
		}

		if (tree.right == null) {
			return false;
		}

		boolean aux = ((tree.left.name + tree.right.name) == (tree.name * 4 + 1));
		return aux && isCorrect(tree.left) && isCorrect(tree.right);
	}

	@Override
	public void run() {
		try {
			Main.barrier.await();
		} catch (BrokenBarrierException | InterruptedException e) {
			e.printStackTrace();
		}

		if (isCorrect(tree))
			System.out.println("Correct");
		else
			System.out.println("Wrong");

	}
}
