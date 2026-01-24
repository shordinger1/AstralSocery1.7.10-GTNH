/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiTextEntry
 * Created by HellFirePvP
 * Date: 15.07.2018 / 14:49
 */
public class GuiTextEntry {

    private String text = "";
    private Runnable changeCallback = null;

    public void setChangeCallback(Runnable changeCallback) {
        this.changeCallback = changeCallback;
    }

    public void setText(@Nullable String newText) {
        if (newText == null) {
            newText = "";
        }
        String prevText = this.text;
        this.text = newText;
        if (!newText.equals(prevText) && changeCallback != null) {
            changeCallback.run();
        }
    }

    @Nonnull
    public String getText() {
        return text;
    }

    public void textboxKeyTyped(char typedChar, int keyCode) {
        // 1.7.10: Check key codes directly instead of using isKeyComboCtrl* methods
        // case 1 = Select All, case 3 = Copy, case 22 = Paste, case 24 = Cut
        if (keyCode == 3) {
            // Ctrl+C - Copy
            GuiScreen.setClipboardString(this.getText());
        } else if (keyCode == 22) {
            // Ctrl+V - Paste
            this.setText(GuiScreen.getClipboardString());
        } else if (keyCode == 24) {
            // Ctrl+X - Cut
            GuiScreen.setClipboardString(this.getText());
            this.setText("");
        } else {
            String text = this.getText();
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    this.setText(text.length() > 1 ? text.substring(0, text.length() - 1) : "");
                    break;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.setText(text + typedChar);
                    }
            }
        }
    }
}
