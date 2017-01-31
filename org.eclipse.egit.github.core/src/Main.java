import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;

public class Main {

	private static GitHubClient client = new GitHubClient();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		client.setOAuth2Token("352b9aaefe815667b8555164ac8225476e931e40");

        String commitId = "dba934795d90a8b89b848f79f8411f94b4a9ac91";
        final String userName = "harsh-groverfk";
        final String repo = "jenkins-demo";

        String pull_request_id = getPullRequestFromCommitID(userName, repo, commitId);
        System.out.println("Pull request of commit '" + commitId + "' is id: " + pull_request_id);

		Review approval = null;
		try {
			approval = getPullRequestApproval(userName, repo, pull_request_id);
//		} catch (NumberFormatException | IOException e) {
		} catch (Exception e) {
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
                return userName + "/" + repo;
			}
		}, "master");
	}

    private static String getPullRequestFromCommitID(final String user, final String repo, String commitID) {
        CommitService commitService = new CommitService(client);
        IssueService issueService = new IssueService(client);

        try {
            RepositoryCommit repositoryCommit = commitService.getCommit(new IRepositoryIdProvider() {
                public String generateId() {
                    return user + "/" + repo;
                }
            }, commitID);
            List<Commit> parents = repositoryCommit.getParents();
            Commit pr_commit = null;
            if (parents.size() != 2) {
                return null;
            } else {
                pr_commit = parents.get(1);
            }
            if (pr_commit != null) {
                SearchIssue searchIssue = issueService.searchIssues(new IRepositoryIdProvider() {
                    public String generateId() {
                        return user + "/" + repo;
                    }
                }, "closed", pr_commit.getSha()).get(0);

                System.out.println(searchIssue.toString());
                return String.valueOf(searchIssue.getNumber());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

	private static Review getPullRequestApproval(final String user, final String repo, String id) throws NumberFormatException, IOException {
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
