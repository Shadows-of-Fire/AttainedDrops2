package shadows.attained.common;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.*;
import shadows.attained.*;
import shadows.attained.util.BulbHelper;

public class BlockVitalized extends Block {

	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 9);

	public BlockVitalized() {
		super(Material.GROUND);
		setRegistryName("vitalized");
		setHardness(0.8F);
		setCreativeTab(ModRegistry.Attained);
		setSoundType(SoundType.GROUND);
		setUnlocalizedName(AttainedDrops.MODID + ".vitalized");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		setDefaultState(blockState.getBaseState().withProperty(getTypeProperty(), Integer.valueOf(0)));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (hand != EnumHand.MAIN_HAND) {
			return false;
		}
		IBlockState k = world.getBlockState(pos);
		if (!world.isRemote) {
			int xp = player.experienceLevel;
			if (player.inventory.getCurrentItem() != null) {
				for (int i = 0; i < BlockBulb.MobDrops.length; i++) {
					if (canPlayerEnrich(world, pos, player, k, i, xp) == true) {
						world.setBlockState(pos, getStateFromMeta(i + 1), 2);
						world.playSound(player, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, (float) 0.6, (float) 0.8);
						if (player.capabilities.isCreativeMode == false) {
							player.inventory.decrStackSize(player.inventory.currentItem, 1);
							player.experienceLevel = (xp - BulbHelper.getXPUse(i));
						}
						return true;
					}
				}
			}
		}
		else {
			int mx = getMetaFromState(k);
			if (mx != 0) {
				player.addChatComponentMessage(new TextComponentString(I18n.format("phrase.AttainedDrops.DirtStart") + TextFormatting.GREEN + " " + I18n.format(BlockBulb.MobDrops[mx - 1].getUnlocalizedName(new ItemStack(BlockBulb.MobDrops[mx - 1])) + ".name")));
			}
			else {
				player.addChatComponentMessage(new TextComponentString(I18n.format("phrase.AttainedDrops.DirtBlank")));
			}
		}
		return false;
	}

	public boolean canPlayerEnrich(World world, BlockPos pos, EntityPlayer player, IBlockState state, int dropNumber, int xp) {
		if (player.inventory.getCurrentItem().getItem() == BlockBulb.MobDrops[dropNumber]) {
			if (getMetaFromState(state) != (dropNumber + 1) && BulbHelper.isDropEnabled(dropNumber)) {
				if (xp >= BulbHelper.getXPUse(dropNumber) || player.capabilities.isCreativeMode) {
					return true;
				}
			}
			chatHelper(world, pos, player, dropNumber, xp);
		}

		return false;
	}

	public static void chatHelper(World world, BlockPos pos, EntityPlayer player, int dropNumber, int xp) {
		if (world.isRemote) {
			if (BulbHelper.isDropEnabled(dropNumber) == false) {
				player.addChatMessage(new TextComponentString(I18n.format("phrase.AttainedDrops.DisabledBulbChat")));
			}
			if (xp < BulbHelper.getXPUse(dropNumber)) {
				player.addChatMessage(new TextComponentString(I18n.format("phrase.AttainedDrops.ShortXPChat")));
			}
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				TYPE
		});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return withType(meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return getType(state);
	}

	public static int getSoilMeta(IBlockState state) {
		return getType(state);
	}

	protected static int getType(IBlockState state) {
		return state.getValue(getTypeProperty()).intValue();
	}

	public IBlockState withType(int type) {
		return getDefaultState().withProperty(getTypeProperty(), Integer.valueOf(type));
	}

	protected static PropertyInteger getTypeProperty() {
		return TYPE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean useExtraInformation) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			list.add("Right click with enabled drop to enrich");
			list.add("Enabled Items:");
			printItems(list);
		}
		else {
			list.add(TextFormatting.GRAY + "Hold Shift");
		}
	}

	public static void printItems(List<String> list) {
		String string = "";
		for (int i = 0; i < 4; i++) {
			if (BulbHelper.isDropEnabled(i)) {
				string = string + BlockBulb.MobDrops[i].getItemStackDisplayName(new ItemStack(BlockBulb.MobDrops[i])) + ", ";
			}
		}
		list.add(TextFormatting.GREEN + string);
		string = "";
		for (int i = 4; i < BlockBulb.MobDrops.length; i++) {
			if (BulbHelper.isDropEnabled(i)) {
				string = string + BlockBulb.MobDrops[i].getItemStackDisplayName(new ItemStack(BlockBulb.MobDrops[i])) + ", ";
			}
		}
		list.add(TextFormatting.GREEN + string);
		return;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		ret.add(new ItemStack(Blocks.DIRT));
		return ret;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}