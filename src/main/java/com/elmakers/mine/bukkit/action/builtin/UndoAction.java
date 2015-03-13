package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.block.BlockBatch;
import com.elmakers.mine.bukkit.api.block.UndoList;
import com.elmakers.mine.bukkit.api.block.UndoQueue;
import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.block.batch.SpellBatch;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class UndoAction extends BaseSpellAction
{
	private String undoListName;
    int timeout;
    boolean targetSelf;
    boolean targetDown;
    boolean targetBlocks;
    boolean cancel;

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters)
    {
        timeout = parameters.getInt("target_timeout", 0);

        // TODO: Use a ContextAction instead?
        targetSelf = parameters.getBoolean("target_self", false);
        targetDown = parameters.getBoolean("target_down", false);
        targetBlocks = parameters.getBoolean("target_blocks", true);
        cancel = parameters.getBoolean("cancel", true);
    }

    @Override
	public SpellResult perform(CastContext context)
	{
		Entity targetEntity = context.getTargetEntity();

        SpellResult result = SpellResult.CAST;
        if (targetSelf) {
            Mage mage = context.getMage();
            targetEntity = context.getEntity();
            context.setTargetName(mage.getName());
            result = SpellResult.ALTERNATE_UP;
        }
        MageController controller = context.getController();
		if (targetEntity != null && controller.isMage(targetEntity))
		{
			Mage targetMage = controller.getMage(targetEntity);

            BlockBatch batch = targetMage.cancelPending();
            if (batch != null) {
                undoListName = (batch instanceof SpellBatch) ? ((SpellBatch)batch).getSpell().getName() : null;
                if (cancel)
                {
                    return SpellResult.DEACTIVATE;
                }
            }

            UndoQueue queue = targetMage.getUndoQueue();
			UndoList undoList = queue.undoRecent(timeout);
			if (undoList != null) {
				undoListName = undoList.getName();
			}
			return undoList != null ? result : SpellResult.NO_TARGET;
		}

        if (!targetBlocks) {
            return SpellResult.NO_TARGET;
        }

        Block targetBlock = context.getTargetBlock();
        if (targetDown) {
            targetBlock = context.getLocation().getBlock();
        }
        Mage mage = context.getMage();
		if (targetBlock != null)
		{
			boolean targetAll = mage.isSuperPowered();
			if (targetAll)
			{
				UndoList undid = controller.undoRecent(targetBlock, timeout);
				if (undid != null) 
				{
					Mage targetMage = undid.getOwner();
					undoListName = undid.getName();
					context.setTargetName(targetMage.getName());
					return result;
				}
			}
			else
			{
                context.setTargetName(mage.getName());
				UndoList undoList = mage.undo(targetBlock);
                if (undoList != null) {
                    undoListName = undoList.getName();
                    return result;
                }
			}
		}
		
		return SpellResult.NO_TARGET;	
	}

    @Override
    public String transformMessage(String message) {
		return message.replace("$spell", undoListName == null ? "Unknown" : undoListName);
	}
}