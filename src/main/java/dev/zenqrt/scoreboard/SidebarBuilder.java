package dev.zenqrt.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.scoreboard.Sidebar;

import java.util.LinkedList;
import java.util.List;

public class SidebarBuilder {

    private final Component title;
    private final List<Line> lines;

    private int emptyLines;

    public SidebarBuilder(Component title) {
        this.title = title;
        this.lines = new LinkedList<>();
    }

    public void setLine(int line, Line sidebarLine) {
        lines.add(line, sidebarLine);
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void addLineAtStart(Line line) {
        setLine(0, line);
    }

    public void emptyLine(int line) {
        setLine(line, createEmptyLine());
    }

    public void emptyLine() {
        addLine(createEmptyLine());
    }

    public void emptyLineAtStart() {
        addLineAtStart(createEmptyLine());
    }

    private Line createEmptyLine() {
        emptyLines++;
        return new Line("empty" + emptyLines, Component.empty());
    }

    public Sidebar build() {
        var sidebar = new Sidebar(title);

        for(int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            sidebar.createLine(new Sidebar.ScoreboardLine(line.id(), line.content(), i));
            System.out.println("Line = " + PlainTextComponentSerializer.plainText().serialize(line.content));
        }

        return sidebar;
    }

    public record Line(String id, Component content) {
    }

}
