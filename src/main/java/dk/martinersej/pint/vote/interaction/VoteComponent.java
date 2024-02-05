package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class VoteComponent {

    @Getter
    private final Sidebar sidebar;
    private final ComponentSidebarLayout componentSidebar;
    private final SidebarAnimation<Component> titleAnimation;
    private final Supplier<Component>[] voteGameSidebarSupplier = new Supplier[3];

    public VoteComponent(@NotNull Sidebar sidebar) {
        this.sidebar = sidebar;

        // Create the title animation
        titleAnimation = createGradientAnimation(
                Component.text("PINT", Style.style(TextDecoration.UNDERLINED)),
                NamedTextColor.YELLOW,
                NamedTextColor.GOLD
        );

        SidebarComponent title = SidebarComponent.animatedLine(titleAnimation);

        for (int i = 0; i < voteGameSidebarSupplier.length; i++) {
            voteGameSidebarSupplier[i] = voteGameSidebarSupplier(i);
        }

        SidebarComponent.Builder builder = SidebarComponent.builder().addBlankLine();

        builder.addStaticLine(Component.text("Vote games", NamedTextColor.LIGHT_PURPLE));
        builder.addBlankLine();

        for (int i = 0; i < 3; i++) {
            builder.addDynamicLine(voteGameSidebarSupplier[i]);
            builder.addBlankLine();
        }

        SidebarComponent lines = builder.build();

        this.componentSidebar = new ComponentSidebarLayout(title, lines);

        // schedule and call tick() every tick
        new BukkitRunnable() {
            @Override
            public void run() {
                if (sidebar.closed()) {
                    cancel();
                    return;
                }
                tick();
            }
        }.runTaskTimerAsynchronously(JavaPlugin.getProvidingPlugin(getClass()), 0, 1L);
    }

    private Supplier<Component> voteGameSidebarSupplier(int index) {
        return () -> {
            if (Pint.getInstance().getGameHandler() != null) {
                Game voteGame = Pint.getInstance().getGameHandler().getGamePool().getVoteGames()[index];
                if (voteGame == null) {
                    return Component.text("No game", NamedTextColor.GRAY);
                }
                int votes = Pint.getInstance().getVoteHandler().gameVotes(voteGame);
                return Component.text(voteGame.getGameInformation().getDisplayName() + " " + votes, NamedTextColor.GRAY);
            }
            return Component.text("No game", NamedTextColor.GRAY);
        };
    }

    // Called every tick
    public void tick() {
        // Advance the animation
        titleAnimation.nextFrame();

        // Update sidebar title & lines
        componentSidebar.apply(sidebar);
    }

    public SidebarAnimation<Component> createGradientAnimation(@NotNull TextComponent text, @NotNull NamedTextColor startColor, @NotNull NamedTextColor endColor) {
        float step = 1f / 8f;

        TagResolver.Single textPlaceholder = Placeholder.component("text", text);
        List<Component> frames = new ArrayList<>((int) (2f / step));

        float phase = -1f;
        while (phase < 1) {
            frames.add(MiniMessage.miniMessage().deserialize("<gradient:" + startColor.toString().toLowerCase() + ":" + endColor.toString().toLowerCase() + ":" + phase + "><text>", textPlaceholder));
            phase += step;
        }

        return new CollectionSidebarAnimation<>(frames);
    }
}
