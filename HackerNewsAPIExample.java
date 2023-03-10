import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import java.util.List;

public class HackerNewsAPIExample {
    private static final String API_BASE_URL = "https://hacker-news.firebaseio.com/v0/";

    public static void main(String[] args) {
        try {
            Gson gson = new Gson();

            // Fetch top 10 stories
            List<Integer> topStoryIds = fetchTopStoryIds();
            System.out.println("Top Stories:");
            for (int i = 0; i < 10; i++) {
                int storyId = topStoryIds.get(i);
                HackerNewsStory story = fetchStory(storyId, gson);
                System.out.println((i+1) + ". " + story.getTitle() + " (" + story.getUrl() + ")");

                // Fetch top 10 comments on each story
                List<Integer> commentIds = story.getKids();
                if (commentIds != null && !commentIds.isEmpty()) {
                    System.out.println("  Top Comments:");
                    for (int j = 0; j < 10 && j < commentIds.size(); j++) {
                        int commentId = commentIds.get(j);
                        HackerNewsComment comment = fetchComment(commentId, gson);
                        System.out.println("  " + (j+1) + ". " + comment.getText());
                    }
                }
            }

            // Fetch past stories
            List<Integer> pastStoryIds = fetchPastStoryIds();
            System.out.println("Past Stories:");
            for (int i = 0; i < 10; i++) {
                int storyId = pastStoryIds.get(i);
                HackerNewsStory story = fetchStory(storyId, gson);
                System.out.println((i+1) + ". " + story.getTitle() + " (" + story.getUrl() + ")");
            }
        } catch (Exception e) {
            System.out.println("Error fetching stories/comments: " + e.getMessage());
        }
    }

    private static List<Integer> fetchTopStoryIds() throws Exception {
        URL url = new URL(API_BASE_URL + "topstories.json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        return gson.fromJson(response.toString(), List.class);
    }

    private static List<Integer> fetchPastStoryIds() throws Exception {
        URL url = new URL(API_BASE_URL + "paststories.json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        return gson.fromJson(response.toString(), List.class);
    }

    private static HackerNewsStory fetchStory(int storyId, Gson gson) throws Exception {
        URL url = new URL(API_BASE_URL + "item/" + storyId + ".json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return gson.fromJson(response.toString(), HackerNewsComment.class);
    }
}

class HackerNewsStory {
    private String title;
    private String url;
    private List<Integer> kids;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public List<Integer> getKids() {
        return kids;
    }
}

class HackerNewsComment {
    private String text;

    public String getText() {
        return text;
    }
}