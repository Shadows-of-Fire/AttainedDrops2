package shadows.attained.items;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import shadows.attained.AttainedDrops;
import shadows.attained.blocks.BlockSoil;
import shadows.attained.init.ModRegistry;

public class ItemSeed extends Item {

	public ItemSeed() {
		super(new Properties().group(AttainedDrops.GROUP));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		EntityPlayer player = ctx.getPlayer();
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof BlockSoil && world.isAirBlock(pos.up())) {
			ItemStack stack = ctx.getItem();
			if (player.canPlayerEdit(pos, ctx.getFace(), stack) && ctx.getFace() == EnumFacing.UP) {
				world.setBlockState(pos.up(), ModRegistry.PLANT.getDefaultState());
				stack.shrink(1);
				world.playSound(player, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, (float) 0.6, (float) 1.0);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag advanced) {
		KeyBinding sneak = Minecraft.getInstance().gameSettings.keyBindSneak;
		if (sneak.isKeyDown()) {
			list.add(new TextComponentTranslation("tooltip.attaineddrops2.plantvitalized"));
		} else {
			list.add(new TextComponentTranslation("tooltip.attaineddrops2.holdshift", new TextComponentTranslation(sneak.getTranslationKey())));
		}
	}

}
