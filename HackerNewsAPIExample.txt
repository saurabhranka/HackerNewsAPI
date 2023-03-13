import java.sql.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class HackerNewsDbExample {
    private static final String API_BASE_URL = "https://hacker-news.firebaseio.com/v0/";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hackernews";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        // Create a cache that can hold up to 100 stories
        Cache<Integer, HackerNewsStory> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build();

        
		
		// Fetch the details of the top stories from the cache, or from the API if not cached
        for (int i = 0; i < 10; i++) {
            int storyId = topStoryIds.get(i);
            HackerNewsStory story = cache.getIfPresent(storyId);
            if (story == null) {
                story = fetchStory(storyId);
                cache.put(storyId, story);
            }
            System.out.println((i+1) + ". " + story.getTitle() + " (" + story.getUrl() + ")");
        }

        // Fetch the details of the top stories from the cache and database, or from the API if not cached or stored in db
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM stories WHERE id = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO stories (id, title, url) VALUES (?, ?, ?)")) {
            for (int i = 0; i < 10; i++) {
                int storyId = topStoryIds.get(i);
                HackerNewsStory story = cache.getIfPresent(storyId);
                if (story == null) {
                    stmt.setInt(1, storyId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        story = new HackerNewsStory(storyId, rs.getString("title"), rs.getString("url"));
                        cache.put(storyId, story);
                    } else {
                        story = fetchStory(storyId);
                        cache.put(storyId, story);
                        insertStmt.setInt(1, storyId);
                        insertStmt.setString(2, story.getTitle());
                        insertStmt.setString(3, story.getUrl());
                        insertStmt.executeUpdate();
                    }
                }
                System.out.println((i+1) + ". " + story.getTitle() + " (" + story.getUrl() + ")");
            }
			
			// Fetch past stories
            List<Integer> pastStoryIds = fetchPastStoryIds();
            System.out.println("Past Stories:");
            for (int i = 0; i < 10; i++) {
                int storyId = pastStoryIds.get(i);
                HackerNewsStory story = fetchStory(storyId, gson);
                System.out.println((i+1) + ". " + story.getTitle() + " (" + story.getUrl() + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> fetchTopStoryIds() {
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

    private static HackerNewsStory fetchStory(int storyId) {
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
    }
}

class HackerNewsStory {
    private int id;
    private String title;
    private String url;

    public HackerNewsStory(int id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}

class HackerNewsComment {
    private String text;
	
	public String setText(){
		return text;
	}

    public String getText() {
        return text;
    }
}
