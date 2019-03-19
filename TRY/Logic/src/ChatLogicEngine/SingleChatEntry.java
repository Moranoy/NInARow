package ChatLogicEngine;

public class SingleChatEntry {
    private final String chatString;
    private final String username;
    private final long time;

    public SingleChatEntry(String chatString, String username) {
        this.chatString = chatString;
        this.username = username;
        this.time = System.currentTimeMillis();
    }

    public String getChatString() {
        return chatString;
    }

    public long getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return (username != null ? username + ": " : "") + chatString;
    }
}
