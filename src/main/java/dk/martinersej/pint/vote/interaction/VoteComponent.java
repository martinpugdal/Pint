package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.Pint;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VoteComponent {

    @Getter
    private final Sidebar sidebar;
    private final ComponentSidebarLayout componentSidebar;
    private final SidebarAnimation<Component> titleAnimation;

    public VoteComponent(@NotNull Sidebar sidebar) {
        this.sidebar = sidebar;

        // Create the title animation
        titleAnimation = createGradientAnimation(
                Component.text("PINT", Style.style(TextDecoration.UNDERLINED, TextDecoration.BOLD)),
                NamedTextColor.GOLD,
                NamedTextColor.YELLOW
        );

        SidebarComponent title = SidebarComponent.animatedLine(titleAnimation);

        SidebarComponent.Builder builder = SidebarComponent.builder().addBlankLine();

        builder.addStaticLine(Component.text("Vote games", NamedTextColor.LIGHT_PURPLE));
        builder.addBlankLine();

        builder.addDynamicLine(() -> {
            if (Pint.getInstance().getVoteHandler() != null &&
                Pint.getInstance().getVoteHandler().getVoteTimer() != null &&
                Pint.getInstance().getGameHandler().getCurrentGame() != null &&
                !Pint.getInstance().getGameHandler().isGameRunning())
            {
                return Component.text("§aSpil: §e" + Pint.getInstance().getGameHandler().getCurrentGame().getGameInformation().getDisplayName());
            }
            return Component.text("");
        });
        builder.addDynamicLine(() -> {
            if (
                Pint.getInstance().getVoteHandler() != null &&
                Pint.getInstance().getVoteHandler().getVoteTimer() != null &&
                Pint.getInstance().getVoteHandler().getCooldown() != Pint.getInstance().getVoteHandler().getFullCooldown() &&
                Pint.getInstance().getGameHandler().getCurrentGame() == null
            ) {
                return Component.text("§aVi finder et spil om: §e" + Pint.getInstance().getVoteHandler().getCooldown());
            } else if (
                Pint.getInstance().getVoteHandler() != null &&
                Pint.getInstance().getVoteHandler().getVoteTimer() != null &&
                Pint.getInstance().getGameHandler().getCurrentGame() != null &&
                !Pint.getInstance().getGameHandler().isGameRunning()
            ) {
                return Component.text("§aVi starter om: §e" + Pint.getInstance().getVoteHandler().getCooldown());
            } else if (Pint.getInstance().getGameHandler().getCurrentGame() != null && Pint.getInstance().getGameHandler().isGameRunning()) {
                return Component.text("§aVi spiller nu: §e" + Pint.getInstance().getGameHandler().getCurrentGame().getGameInformation().getDisplayName());
            }
            return Component.text("§6Stem på et spil");
        });

        this.componentSidebar = new ComponentSidebarLayout(title, builder.build());
    }

    // Called every tick
    public void tick() {
        if (sidebar.closed()) {
            return;
        }
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
