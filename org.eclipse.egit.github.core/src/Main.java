import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.Review;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.PullRequestService;

public class Main {

	private static GitHubClient client = new GitHubClient();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		client.setOAuth2Token("352b9aaefe815667b8555164ac8225476e931e40");


		Review approval = null;
		try {
			approval = getPullRequestApproval("harsh-groverfk", "jenkins-demo", "16");
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (approval != null){
			System.out.println("Pull request approved by : " + approval.getUser().getLogin());
		} else {
			System.out.println("Pull request not approved.");
		}

		System.out.println();
		System.out.println("******************************");
		System.out.println();

		/*
		List<RepositoryCommit> commits = getCommitsFromPullRequest(new IRepositoryIdProvider() {

			@Override
			public String generateId() {
				return "harsh-groverfk/jenkins-demo";
			}
		}, 16);
		*/

		verifyLatestMasterCommit(new IRepositoryIdProvider() {

			@Override
			public String generateId() {
				return "dan13ram/Graph";
			}
		}, "master");
	}

	private static boolean verifyLatestMasterCommit(IRepositoryIdProvider iRepositoryIdProvider, String branch) {
		client.setHeaderAccept("application/vnd.github.cryptographer-preview");
		CommitService commitService = new CommitService(client);
		try {
			List<RepositoryCommit> repoCommits = commitService.getCommits(iRepositoryIdProvider, branch, null);
			System.out.println("The latest commit is : " + repoCommits.get(0).getSha());

			return verifyCommit(iRepositoryIdProvider, repoCommits.get(0).getSha());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static boolean verifyCommit(IRepositoryIdProvider iRepositoryIdProvider, String sha) {
		client.setHeaderAccept("application/vnd.github.cryptographer-preview");
		CommitService commitService = new CommitService(client);

		try {
			RepositoryCommit repoCommit = commitService.getCommit(iRepositoryIdProvider, sha);

			if (isVerifiedRepositoryCommit(repoCommit)) {
				System.out.println("This is a verified commit!");
				return true;
			} else {
				System.out.println("This is not a verified commit :(");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private static List<RepositoryCommit> getCommitsFromPullRequest(IRepositoryIdProvider iRepositoryIdProvider, int pullId) {
		PullRequestService pr_service = new PullRequestService(client);
		List<RepositoryCommit> repoCommits = null;
		try {
			repoCommits = pr_service.getCommits(iRepositoryIdProvider, pullId);
			for (RepositoryCommit c : repoCommits) {
				System.out.println(c.getCommit().getMessage());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return repoCommits;
	}

	private static Review getPullRequestApproval(String user, String repo, String id) throws NumberFormatException, IOException {
		client.setHeaderAccept("application/vnd.github.black-cat-preview+json");
		PullRequestService pr_service = new PullRequestService(client);

		PullRequest pullRequest = pr_service.getPullRequest(new IRepositoryIdProvider() {

			@Override
			public String generateId() {
				// TODO Auto-generated method stub
				return user + "/" + repo;
			}
		}, Integer.parseInt(id));
		System.out.println("Pull request sent by : " + pullRequest.getUser().getLogin());

		List<Review> reviews = null;
		try {
			reviews = pr_service.getReviews(user, repo, id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Review r : reviews) {
			System.out.println(r.getState());
			if (r.getState().equals("APPROVED")) {
				return r;
			}
		}

		return null;
	}

	private static boolean isVerifiedRepositoryCommit(RepositoryCommit repoCommit) {
		Commit commit = repoCommit.getCommit();

		if (commit.getVerification() != null && commit.getVerification().isVerified()) {
			return true;
		}

		return false;
	}
}
