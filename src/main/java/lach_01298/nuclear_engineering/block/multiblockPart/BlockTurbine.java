package lach_01298.nuclear_engineering.block.multiblockPart;

import lach_01298.nuclear_engineering.NuclearEngineering;
import lach_01298.nuclear_engineering.inventory.NEFaces;
import lach_01298.nuclear_engineering.item.render.ItemModelProvider;
import lach_01298.nuclear_engineering.tile.machine.TileEntityGrinder;
import lach_01298.nuclear_engineering.tile.multiblock.TileEntityHeatExchanger;
import lach_01298.nuclear_engineering.tile.multiblock.TileEntityMultiblock;
import lach_01298.nuclear_engineering.tile.multiblock.TileEntityReactorCore;
import lach_01298.nuclear_engineering.tile.multiblock.TileEntityTurbine;
import lach_01298.nuclear_engineering.util.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockTurbine extends BlockMultiblockPart implements ItemModelProvider 
{


	
	private String name;
	public BlockTurbine(String name)
	{
		super(Material.IRON);
		this.name = name;
		
		setRegistryName(name);
		setCreativeTab(NuclearEngineering.NETab);
		setUnlocalizedName(NuclearEngineering.MOD_ID+"."+name);
		setHardness(Constants.HARDNESS_METAL);
		setResistance(Constants.RESISTANCE_METAL);
	}

	
	


	@Override
	public void registerItemModel(Item itemBlock)
	{
		NuclearEngineering.proxy.registerItemRenderer(itemBlock,0 , this.name);
	}
	

	 public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	 {
		 if(!world.isRemote)
		 {
		 findMaster(world, pos,false); 
		 }
	 }


	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityTurbine();
		
	}
	
	public boolean findMaster(World world, BlockPos pos,boolean setFirstblockToMaster)
	{
		
		if(world.getTileEntity(pos) instanceof TileEntityTurbine)
		{
			
			TileEntityTurbine tile = (TileEntityTurbine) world.getTileEntity(pos);
			
			for(int x = -1; x <= 1; x++)
			{
				
				for(int y = -1; y <= 1; y++)
				{
					for(int z = -1; z <= 1; z++)
					{
						
						if((world.getTileEntity(pos.add(x, y, z)) instanceof TileEntityTurbine) && (pos.add(x, y, z)!= pos))
						{
							
							TileEntityTurbine core = (TileEntityTurbine) world.getTileEntity(pos.add(x, y, z));
							if(setFirstblockToMaster)
							{
								tile.setMasterTile(core);
							}
							else
							{
								tile.setMasterTile(core.getMasterTile());
							}
							return true;
						}
					}
				}
			}
			tile.setMasterTile(tile);
		}
	
		return false;

	}
	
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
	{
		if(world.getTileEntity(pos) instanceof TileEntityTurbine)
		{
			TileEntityTurbine tile = (TileEntityTurbine) world.getTileEntity(pos);
			if(tile.isMasterTile())
			{
				tile.getMasterTile().checkMultiBlock(null);
			}
		}
		System.out.println("here");
		
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if(world.getTileEntity(pos) instanceof TileEntityTurbine && !world.isRemote)
		{
			TileEntityTurbine tile = (TileEntityTurbine) world.getTileEntity(pos);
			if(tile.isMasterTile())
			{
				
				findMaster(world, pos, true);
				
				tile.getMasterTile().setMasterTile(tile.getMasterTile());
				tile.getMasterTile().checkMultiBlock(null);
				
				
				
			}

			super.breakBlock(world, pos, state);
		}
	}
	
 }

