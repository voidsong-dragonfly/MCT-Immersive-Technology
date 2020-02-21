package mctmods.immersivetechnology.common.blocks.metal.tileentities;

import blusunrize.immersiveengineering.common.util.Utils;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.api.ITUtils;
import mctmods.immersivetechnology.api.client.MechanicalEnergyAnimation;
import mctmods.immersivetechnology.api.crafting.SteamTurbineRecipe;
import mctmods.immersivetechnology.common.Config.ITConfig.Machines.SteamTurbine;
import mctmods.immersivetechnology.common.Config.ITConfig.MechanicalEnergy;
import mctmods.immersivetechnology.common.util.ITSounds;
import mctmods.immersivetechnology.common.util.network.MessageStopSound;
import mctmods.immersivetechnology.common.util.sound.ITSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySteamTurbineMaster extends TileEntitySteamTurbineSlave {

	private static int maxSpeed = MechanicalEnergy.mechanicalEnergy_speed_max;
	private static int speedGainPerTick = SteamTurbine.steamTurbine_speed_gainPerTick;
	private static int speedLossPerTick = SteamTurbine.steamTurbine_speed_lossPerTick;
	private static int inputTankSize = SteamTurbine.steamTurbine_input_tankSize;
	private static int outputTankSize = SteamTurbine.steamTurbine_input_tankSize;
	private static float maxRotationSpeed = SteamTurbine.steamTurbine_speed_maxRotation;

	public FluidTank[] tanks = new FluidTank[] {
		new FluidTank(inputTankSize),
		new FluidTank(outputTankSize)
	};

	public int burnRemaining = 0;
	public int speed;

	public static BlockPos fluidOutputPos;

	public SteamTurbineRecipe lastRecipe;

	MechanicalEnergyAnimation animation = new MechanicalEnergyAnimation();

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
		tanks[0].readFromNBT(nbt.getCompoundTag("tank0"));
		tanks[1].readFromNBT(nbt.getCompoundTag("tank1"));
		speed = nbt.getInteger("speed");
		animation.readFromNBT(nbt);
		burnRemaining = nbt.getInteger("burnRemaining");
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		nbt.setTag("tank0", tanks[0].writeToNBT(new NBTTagCompound()));
		nbt.setTag("tank1", tanks[1].writeToNBT(new NBTTagCompound()));
		nbt.setInteger("speed", speed);
		animation.writeToNBT(nbt);
		nbt.setInteger("burnRemaining", burnRemaining);
	}

	private void speedUp() {
		speed = Math.min(maxSpeed, speed + speedGainPerTick);
	}

	private void speedDown() {
		speed = Math.max(0, speed - speedLossPerTick);
	}

	private void pumpOutputOut() {
		if(tanks[1].getFluidAmount() == 0) return;
		if(fluidOutputPos == null) fluidOutputPos = ITUtils.LocalOffsetToWorldBlockPos(this.getPos(), 0, 2, 8, facing);
		IFluidHandler output = FluidUtil.getFluidHandler(world, fluidOutputPos, facing.getOpposite());
		if(output == null) return;
		FluidStack out = tanks[1].getFluid();
		int accepted = output.fill(out, false);
		if(accepted == 0) return;
		int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.amount, accepted), false), true);
		this.tanks[1].drain(drained, true);
		efficientMarkDirty();
		this.markContainingBlockForUpdate(null);
	}

	public void handleSounds() {
		float level = (float) speed / maxSpeed;
		BlockPos center = getPos().offset(facing, 5);
		if(level == 0) ITSoundHandler.StopSound(center);
		else {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			float attenuation = Math.max((float) player.getDistanceSq(center.getX(), center.getY(), center.getZ()) / 8, 1);
			ITSoundHandler.PlaySound(center, ITSounds.turbine, SoundCategory.BLOCKS, true, (10 * level) / attenuation, level);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onChunkUnload() {
		ITSoundHandler.StopSound(getPos().offset(facing, 5));
		super.onChunkUnload();
	}

	@Override
	public void disassemble() {
		BlockPos center = getPos().offset(facing, 5);
		ImmersiveTechnology.packetHandler.sendToAllTracking(new MessageStopSound(center), new NetworkRegistry.TargetPoint(world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 0));
		super.disassemble();
	}

	public void efficientMarkDirty() { // !!!!!!! only use it within update() function !!!!!!!
		world.getChunkFromBlockCoords(this.getPos()).markDirty();
	}

	@Override
	public void update() {
		if(world.isRemote) {
			handleSounds();
			return;
		}
		float rotationSpeed = speed == 0 ? 0f : ((float) speed / (float) maxSpeed) * maxRotationSpeed;
		if(ITUtils.setRotationAngle(animation, rotationSpeed)) {
			efficientMarkDirty();
			this.markContainingBlockForUpdate(null);
		}
		if(burnRemaining > 0) {
			burnRemaining--;
			speedUp();
		} else if(!isRSDisabled() && tanks[0].getFluid() != null && tanks[0].getFluid().getFluid() != null && ITUtils.checkMechanicalEnergyReceiver(world, getPos()) && ITUtils.checkAlternatorStatus(world, getPos())) {
			SteamTurbineRecipe recipe = (lastRecipe != null && tanks[0].getFluid().isFluidEqual(lastRecipe.fluidInput)) ? lastRecipe : SteamTurbineRecipe.findFuel(tanks[0].getFluid());
			if(recipe != null && recipe.fluidInput.amount <= tanks[0].getFluidAmount()) {
				lastRecipe = recipe;
				burnRemaining = recipe.getTotalProcessTime();
				tanks[0].drain(recipe.fluidInput.amount, true);
				if(recipe.fluidOutput != null) tanks[1].fill(recipe.fluidOutput, true);
				this.markContainingBlockForUpdate(null);
				speedUp();
			} else speedDown();
		} else speedDown();
		pumpOutputOut();
	}

	@Override
	public boolean isDummy() {
		return false;
	}

	@Override
	public TileEntitySteamTurbineMaster master() {
		master = this;
		return this;
	}

}