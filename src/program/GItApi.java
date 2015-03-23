package program;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GItApi {
	
	public static int getSize(String dirPath, String fileName, String commitId) throws MissingObjectException, IncorrectObjectTypeException, IOException, NullPointerException {
		File gitWorkDir = new File(dirPath);
		Repository localRepo =Git.open(gitWorkDir).getRepository();
		ObjectId id=localRepo.resolve(commitId);
		RawText file = null;
		ObjectReader reader = localRepo.newObjectReader();

		try {
		// Get the commit object for that revision
		RevWalk walk = new RevWalk(reader);
		RevCommit commit = walk.parseCommit(id);

		// Get the revision's file tree
		RevTree tree = commit.getTree();
		// .. and narrow it down to the single file's path
		TreeWalk treewalk = TreeWalk.forPath(reader, fileName, tree);

		if (treewalk != null) {
		// use the blob id to read the file's data
		byte[] data = reader.open(treewalk.getObjectId(0)).getBytes();
		//return new String(data, "utf-8");
		file = new RawText(data);
		//System.out.println(new String(data, "utf-8"));

		//newText = new RawText(data);
		} else {
		System.out.println("File Not Found");
		}
		} catch (NullPointerException e){
		return -1;
		}
		finally {
		reader.release();
		}

		return file.size();

		}

	
}
