package dev.zenqrt.clownchase.sidebar.sidebars;

import dev.zenqrt.clownchase.sidebar.PacketSidebar;
import dev.zenqrt.clownchase.utils.text.Messages;
import dev.zenqrt.clownchase.utils.text.TextColorPresets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class ClownChaseSidebar extends PacketSidebar {

    private static final String GAME_SCOREBOARD_TITLE = "game.scoreboard.title";
    private static final String GAME_SCOREBOARD_CLOWN_DISTANCE = "game.scoreboard.clown_distance";
    private static final String GAME_SCOREBOARD_YOUR_SCORE = "game.scoreboard.your_score";
    private static final String GAME_SCOREBOARD_THIRD_PLACE_SCORE = "game.scoreboard.third_place_score";
    private static final String GAME_SCOREBOARD_SECOND_PLACE_SCORE = "game.scoreboard.second_place_score";
    private static final String GAME_SCOREBOARD_FIRST_PLACE_SCORE = "game.scoreboard.first_place_score";
    private static final String GAME_SCOREBOARD_SCORE_HEADER = "game.scoreboard.score_header";
    private static final String GAME_SCOREBOARD_MAP = "game.scoreboard.map";

    public ClownChaseSidebar(String mapName) {
        super(Component.translatable(GAME_SCOREBOARD_TITLE, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));

        this.addLine("clown_distance", clownDistanceText(0));
        this.addLine("your_score", yourScoreText(0));
        this.addEmptyLine();
        this.addLine("third_place_score", thirdPlaceScoreText("...", 0));
        this.addLine("second_place_score", secondPlaceScoreText("...", 0));
        this.addLine("first_place_score", firstPlaceScoreText("...", 0));
        this.addLine("score_header", Component.translatable(GAME_SCOREBOARD_SCORE_HEADER, TextColorPresets.SCOREBOARD_TEXT));
        this.addEmptyLine();
        this.addLine("map", mapComponent(mapName));
    }

    public void setClownDistance(int distance) {
        this.updateLine("clown_distance", clownDistanceText(distance));
    }

    public void setYourScore(int score) {
        this.updateLine("your_score", yourScoreText(score));
    }

    public void setThirdPlaceScore(String username, int score) {
        this.updateLine("third_place_score", thirdPlaceScoreText(username, score));
    }

    public void setSecondPlaceScore(String username, int score) {
        this.updateLine("second_place_score", secondPlaceScoreText(username, score));
    }

    public void setFirstPlaceScore(String username, int score) {
        this.updateLine("first_place_score", firstPlaceScoreText(username, score));
    }

    private static Component mapComponent(String mapName) {
        return Component.translatable(GAME_SCOREBOARD_MAP, TextColorPresets.SCOREBOARD_TEXT, Component.text(mapName, NamedTextColor.WHITE));
    }

    private static Component firstPlaceScoreText(String username, int score) {
        return Component.translatable(GAME_SCOREBOARD_FIRST_PLACE_SCORE, NamedTextColor.WHITE, Component.text(username), Messages.candyText(score));
    }

    private static Component secondPlaceScoreText(String username, int score) {
        return Component.translatable(GAME_SCOREBOARD_SECOND_PLACE_SCORE, NamedTextColor.WHITE, Component.text(username), Messages.candyText(score));
    }

    private static Component thirdPlaceScoreText(String username, int score) {
       return Component.translatable(GAME_SCOREBOARD_THIRD_PLACE_SCORE, NamedTextColor.WHITE, Component.text(username), Messages.candyText(score));
    }

    private static Component yourScoreText(int score) {
        return Component.translatable(GAME_SCOREBOARD_YOUR_SCORE, NamedTextColor.GREEN, Messages.candyText(score));
    }

    private static Component clownDistanceText(int distance) {
        Component indicator =
                distance <= 5 ? Component.text("<5m", NamedTextColor.RED) :
                        distance <= 15 ? Component.text("<15m", NamedTextColor.GOLD) :
                                distance <= 30 ? Component.text("<30m", NamedTextColor.YELLOW) :
                                        Component.text("FAR", NamedTextColor.GRAY);

        return Component.translatable(GAME_SCOREBOARD_CLOWN_DISTANCE, NamedTextColor.GREEN, indicator.decorate(TextDecoration.BOLD));
    }
}
