package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

public final class Managers {

    private Managers () {}

    public static HTTPTaskManager getDefault(URI uri) throws IOException, InterruptedException {

        return new HTTPTaskManager(uri);
    }

    public static InMemoryHistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {

        return new Gson();
    }
}
