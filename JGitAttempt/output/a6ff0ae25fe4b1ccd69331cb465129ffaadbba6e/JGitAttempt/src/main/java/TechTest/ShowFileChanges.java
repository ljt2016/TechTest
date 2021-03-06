diff --git a/JGitAttempt/src/main/java/TechTest/ShowFileChanges.java b/JGitAttempt/src/main/java/TechTest/ShowFileChanges.java
new file mode 100644
index 0000000..4343085
--- /dev/null
+++ b/JGitAttempt/src/main/java/TechTest/ShowFileChanges.java
@@ -0,0 +1,67 @@
+package TechTest;
+
+import java.io.IOException;
+import java.util.List;
+
+import org.eclipse.jgit.api.Git;
+import org.eclipse.jgit.api.errors.GitAPIException;
+import org.eclipse.jgit.diff.DiffEntry;
+import org.eclipse.jgit.diff.DiffFormatter;
+import org.eclipse.jgit.lib.ObjectId;
+import org.eclipse.jgit.lib.ObjectReader;
+import org.eclipse.jgit.lib.Repository;
+import org.eclipse.jgit.revwalk.RevCommit;
+import org.eclipse.jgit.revwalk.RevTree;
+import org.eclipse.jgit.revwalk.RevWalk;
+import org.eclipse.jgit.treewalk.AbstractTreeIterator;
+import org.eclipse.jgit.treewalk.CanonicalTreeParser;
+
+import TechTest.helper.JGitHelper;
+
+/**
+ *  @author   :   Magister
+ *  @fileName :   TechTest.ShowFileChanges.java
+ *  
+ *  2018年3月5日	下午3:10:25  
+*/
+
+public class ShowFileChanges {
+
+	public static void main(String[] args) throws IOException, GitAPIException {
+		try(Repository repo = JGitHelper.openJGitCookbookRepository()){
+			AbstractTreeIterator oldTreeParser = prepareTreeParser(repo, "2a942bd5bad3c9e4b2aab738efb39970615d5642");
+			AbstractTreeIterator newTreeParser = prepareTreeParser(repo, "65997a82cf1ecb7b6612a63885e54ca71a868bbc");
+			
+			try(Git git = new Git(repo)){
+				List<DiffEntry> diffs = git.diff().
+						setOldTree(oldTreeParser).
+						setNewTree(newTreeParser).
+//						setPathFilter(PathSuffixFilter.create(".java")).
+						call();
+				for(DiffEntry entry: diffs) {
+					System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
+					try(DiffFormatter formatter = new DiffFormatter(System.out)){
+						formatter.setRepository(repo);
+						formatter.format(entry);
+					}
+				}
+			}
+		}
+		System.out.println("End!");
+	}
+	
+	private static AbstractTreeIterator prepareTreeParser(Repository repo, String cid) throws IOException {
+		try(RevWalk walk = new RevWalk(repo)){
+			RevCommit commit = walk.parseCommit(ObjectId.fromString(cid));
+			RevTree tree = walk.parseTree(commit.getTree().getId());
+			
+			CanonicalTreeParser treeParser = new CanonicalTreeParser();
+			try(ObjectReader reader = repo.newObjectReader()){
+				treeParser.reset(reader, tree.getId());
+			}
+			walk.dispose();
+			return treeParser;
+		}
+	}
+
+}
