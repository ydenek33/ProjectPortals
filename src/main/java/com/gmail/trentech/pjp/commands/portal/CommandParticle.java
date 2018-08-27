package com.gmail.trentech.pjp.commands.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleEffect.Builder;
import org.spongepowered.api.effect.particle.ParticleOption;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjp.effects.Colors;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.portal.PortalService;
import com.gmail.trentech.pjp.portal.features.Properties;

public class CommandParticle implements CommandCallable {
	
	private final Help help = Help.get("portal particle").get();

	@SuppressWarnings("rawtypes")
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("particle")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		Portal portal;

		try {
			String portalName = args[0];
			
			Optional<Portal> optionalPortal = Sponge.getServiceManager().provideUnchecked(PortalService.class).get(portalName, PortalType.PORTAL);
			
			if(!optionalPortal.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, portalName, " does not exist"), false);
			}
			portal = optionalPortal.get();
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}

		ParticleType particle;
		
		try {
			String[] particleId = args[1].split(":");
			
			Optional<ParticleType> optionalParticle = Sponge.getRegistry().getType(ParticleType.class, CatalogKey.of(particleId[0], particleId[1]));
			
			if(!optionalParticle.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, "Not a valid ParticleType"), true);
			}
			particle = optionalParticle.get();
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		int intensity;
		
		try {
			String intensityString = args[2];
			
			try {
				intensity = Integer.parseInt(intensityString);
			} catch(Exception e) {
				throw new CommandException(Text.of(TextColors.RED, "Not a valid Integer"), true);
			}			
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		Builder builder = ParticleEffect.builder().type(particle);
		if(args.length > 3) {
			try {
				String[] particleOption = args[3].split(":");
				
				Optional<ParticleOption> optionalOption = Sponge.getRegistry().getType(ParticleOption.class, CatalogKey.of(particleOption[0], particleOption[1]));

				if(!optionalOption.isPresent()) {
					throw new CommandException(Text.of(TextColors.RED, "Not a valid ParticleOption"), false);
				}
			} catch(Exception e) {
				throw new CommandException(getHelp().getUsageText());
			}

			String value;
			String previous;
			try {
				value = args[4];
				previous = args[3];
			} catch(Exception e) {
				throw new CommandException(getHelp().getUsageText());
			}

	    	if(previous.equalsIgnoreCase(ParticleOptions.BLOCK_STATE.getKey().toString())) {
	    		String id[] = value.split(":");
	    		
	    		if(id.length < 2) {
	    			throw new CommandException(Text.of(TextColors.RED, "Not a valid BlockType"), false);
	    		}
				Optional<BlockType> optionalBlockType = Sponge.getRegistry().getType(BlockType.class, CatalogKey.of(id[0], id[1]));
				
				if(!optionalBlockType.isPresent()) {
					throw new CommandException(Text.of(TextColors.RED, "Not a valid BlockType"), false);
				}
				
				BlockState state = optionalBlockType.get().getDefaultState();
				
	    		if(id.length > 2) {
					try {
						Integer.parseInt(id[2]);
					} catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, "Not a valid Data Value"), false);
					}
					
					DataContainer container = state.toContainer();
					DataQuery query = DataQuery.of('/', "UnsafeDamage");
					
					container.set(query, Integer.parseInt(id[2]));
					
					state = Sponge.getDataManager().deserialize(BlockState.class, container).get();
	    		}
	    		
	    		builder.option(ParticleOptions.BLOCK_STATE, state);
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.COLOR.getKey().toString())) {
	    		Optional<Color> color = Colors.get(value);
	    		
	    		if(!color.isPresent()) {
	    			throw new CommandException(Text.of(TextColors.RED, "Not a valid Color"), false);
	    		}
	    		builder.option(ParticleOptions.COLOR, color.get());
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.DIRECTION.getKey().toString())) {
	    		builder.option(ParticleOptions.DIRECTION, Direction.valueOf(value));
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.FIREWORK_EFFECTS.getKey().toString())) {

	    	} else if(previous.equalsIgnoreCase(ParticleOptions.ITEM_STACK_SNAPSHOT.getKey().toString())) {
	    		String id[] = value.split(":");
	    		
	    		if(id.length < 2) {
	    			throw new CommandException(Text.of(TextColors.RED, "Not a valid ItemType"), false);
	    		}
				Optional<ItemType> optionalItemType = Sponge.getRegistry().getType(ItemType.class, CatalogKey.of(id[0], id[1]));
				
				if(!optionalItemType.isPresent()) {
					throw new CommandException(Text.of(TextColors.RED, "Not a valid ItemType"), false);
				}
				
				ItemStack itemStack = ItemStack.of(optionalItemType.get(), 1);
				
	    		if(id.length > 2) {
					try {
						Integer.parseInt(id[2]);
					} catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, "Not a valid Data Value"), false);
					}
					
					DataContainer container = itemStack.toContainer();
					DataQuery query = DataQuery.of('/', "UnsafeDamage");
					
					container.set(query, Integer.parseInt(id[2]));
					
					itemStack = Sponge.getDataManager().deserialize(ItemStack.class, container).get();
	    		}	    		
	    		builder.option(ParticleOptions.ITEM_STACK_SNAPSHOT, itemStack.createSnapshot());
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.NOTE.getKey().toString())) {
	    		String[] key = value.split(":");
	    		Optional<NotePitch> notepitch = Sponge.getRegistry().getType(NotePitch.class, CatalogKey.of(key[0], key[1]));
	    		
	    		if(!notepitch.isPresent()) {
	    			throw new CommandException(Text.of(TextColors.RED, "Not a valid NotePitch"), false);
	    		}
	    		builder.option(ParticleOptions.NOTE, notepitch.get());
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.OFFSET.getKey().toString())) {
	    		
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.POTION_EFFECT_TYPE.getKey().toString())) {
	    		String[] key = value.split(":");
	    		Optional<PotionEffectType> potionEffect = Sponge.getRegistry().getType(PotionEffectType.class, CatalogKey.of(key[0], key[1]));
	    		
	    		if(!potionEffect.isPresent()) {
	    			throw new CommandException(Text.of(TextColors.RED, "Not a valid PotionEffectType"), false);
	    		}
	    		builder.option(ParticleOptions.POTION_EFFECT_TYPE, potionEffect.get());
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.QUANTITY.getKey().toString())) {
	    		int quantity;
	    		try {
	    			quantity = Integer.parseInt(value);
	    		} catch (Exception e) {
	    			throw new CommandException(Text.of(TextColors.RED, "Not a valid Integer"), false);
	    		}
	    		builder.option(ParticleOptions.QUANTITY, quantity);
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.SCALE.getKey().toString())) {
	    		double scale;
	    		try {
	    			scale = Double.parseDouble(value);
	    		} catch (Exception e) {
	    			throw new CommandException(Text.of(TextColors.RED, "Not a valid Double"), false);
	    		}
	    		builder.option(ParticleOptions.SCALE, scale);
	    	} else if(previous.equalsIgnoreCase(ParticleOptions.SLOW_HORIZONTAL_VELOCITY.getKey().toString())) {

	    	} else if(previous.equalsIgnoreCase(ParticleOptions.VELOCITY.getKey().toString())) {

	    	}
		}

		Properties properties = portal.getProperties().get();
		
		properties.setParticle(Optional.of(builder.build()));
		properties.setIntensity(intensity);
		
		portal.setProperties(properties);
		
		Sponge.getServiceManager().provide(PortalService.class).get().update(portal);
		
		return CommandResult.success();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("particle")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length == 1) {
			for(Portal portal : Sponge.getServiceManager().provideUnchecked(PortalService.class).all(PortalType.PORTAL)) {
				if(portal.getName().equalsIgnoreCase(args[0])) {
					for(ParticleType particleType : Sponge.getRegistry().getAllOf(ParticleType.class)) {
						list.add(particleType.getKey().toString());
					}
					return list;
				}
				
				if(portal.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(portal.getName());
				}
			}
		}
		
		if(args.length == 2) {
			for(ParticleType particleType : Sponge.getRegistry().getAllOf(ParticleType.class)) {
				if(particleType.getKey().toString().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
					for(ParticleOption particleOption : Sponge.getRegistry().getAllOf(ParticleOption.class)) {
						list.add(particleOption.getKey().toString());
					}
					return list;
				}
				
				if(particleType.getKey().toString().toLowerCase().startsWith(args[1].toLowerCase())) {
					list.add(particleType.getKey().toString());
				}
			}
		}
		
		if(args.length == 4) {
			for(ParticleOption particleOption : Sponge.getRegistry().getAllOf(ParticleOption.class)) {
				if(particleOption.getKey().toString().toLowerCase().equalsIgnoreCase(args[3].toLowerCase())) {
			    	if(args[3].equalsIgnoreCase(ParticleOptions.BLOCK_STATE.getKey().toString())) {
			    		for(BlockType blockType : Sponge.getRegistry().getAllOf(BlockType.class)) {
							list.add(blockType.getKey().toString());
			    		}
			    	} else if(args[3].equalsIgnoreCase(ParticleOptions.COLOR.getKey().toString())) {
			    		for(Colors color : Colors.values()) {
							list.add(color.getName());
			    		}
			    	} else if(args[3].equalsIgnoreCase(ParticleOptions.DIRECTION.getKey().toString())) {
			    		for(Direction direction : Direction.values()) {
							list.add(direction.name());
			    		}
			    	} else if(args[3].equalsIgnoreCase(ParticleOptions.FIREWORK_EFFECTS.getKey().toString())) {
			    		// IMPLEMENT
			    	} else if(args[3].equalsIgnoreCase(ParticleOptions.ITEM_STACK_SNAPSHOT.getKey().toString())) {
			    		for(ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class)) {
							list.add(itemType.getKey().toString());
			    		}
			    	} else if(args[3].equalsIgnoreCase(ParticleOptions.NOTE.getKey().toString())) {
			    		for(NotePitch notePitch : Sponge.getRegistry().getAllOf(NotePitch.class)) {
							list.add(notePitch.getKey().toString());
			    		}
			    	} else if(args[3].equalsIgnoreCase(ParticleOptions.POTION_EFFECT_TYPE.getKey().toString())) {
			    		for(PotionEffectType potionEffect : Sponge.getRegistry().getAllOf(PotionEffectType.class)) {
							list.add(potionEffect.getKey().toString());
			    		}
			    	} else if(args[3].equalsIgnoreCase(ParticleOptions.SLOW_HORIZONTAL_VELOCITY.getKey().toString())) {
			    		list.add("true");
			    		list.add("false");
			    	}
					return list;
				}
				
				if(particleOption.getKey().toString().toLowerCase().startsWith(args[3].toLowerCase())) {
					list.add(particleOption.getKey().toString());
				}
			}
		}
		
		if(args.length == 5) {
	    	if(args[3].equalsIgnoreCase(ParticleOptions.BLOCK_STATE.getKey().toString())) {
	    		for(BlockType blockType : Sponge.getRegistry().getAllOf(BlockType.class)) {
					if(blockType.getKey().toString().toLowerCase().equalsIgnoreCase(args[4].toLowerCase())) {
						return list;
					}
					
					if(blockType.getKey().toString().toLowerCase().startsWith(args[4].toLowerCase())) {
						list.add(blockType.getKey().toString());
					}
	    		}
	    	} else if(args[3].equalsIgnoreCase(ParticleOptions.COLOR.getKey().toString())) {
	    		for(Colors color : Colors.values()) {
					if(color.getName().toLowerCase().equalsIgnoreCase(args[4].toLowerCase())) {
						return list;
					}
					
					if(color.getName().toLowerCase().startsWith(args[4].toLowerCase())) {
						list.add(color.getName());
					}
	    		}
	    	} else if(args[3].equalsIgnoreCase(ParticleOptions.DIRECTION.getKey().toString())) {
	    		for(Direction direction : Direction.values()) {
					if(direction.name().toLowerCase().equalsIgnoreCase(args[4].toLowerCase())) {
						return list;
					}
					
					if(direction.name().toLowerCase().startsWith(args[4].toLowerCase())) {
						list.add(direction.name());
					}
	    		}
	    	} else if(args[3].equalsIgnoreCase(ParticleOptions.FIREWORK_EFFECTS.getKey().toString())) {
	    		// IMPLEMENT
	    	} else if(args[3].equalsIgnoreCase(ParticleOptions.ITEM_STACK_SNAPSHOT.getKey().toString())) {
	    		for(ItemType itemType : Sponge.getRegistry().getAllOf(ItemType.class)) {
					if(itemType.getKey().toString().toLowerCase().equalsIgnoreCase(args[4].toLowerCase())) {
						return list;
					}
					
					if(itemType.getKey().toString().toLowerCase().startsWith(args[4].toLowerCase())) {
						list.add(itemType.getKey().toString());
					}
	    		}
	    	} else if(args[3].equalsIgnoreCase(ParticleOptions.NOTE.getKey().toString())) {
	    		for(NotePitch notePitch : Sponge.getRegistry().getAllOf(NotePitch.class)) {
					if(notePitch.getKey().toString().toLowerCase().equalsIgnoreCase(args[4].toLowerCase())) {
						return list;
					}
					
					if(notePitch.getKey().toString().toLowerCase().startsWith(args[4].toLowerCase())) {
						list.add(notePitch.getKey().toString());
					}
	    		}
	    	} else if(args[3].equalsIgnoreCase(ParticleOptions.POTION_EFFECT_TYPE.getKey().toString())) {
	    		for(PotionEffectType potionEffect : Sponge.getRegistry().getAllOf(PotionEffectType.class)) {
					if(potionEffect.getKey().toString().toLowerCase().equalsIgnoreCase(args[4].toLowerCase())) {
						return list;
					}
					
					if(potionEffect.getKey().toString().toLowerCase().startsWith(args[4].toLowerCase())) {
						list.add(potionEffect.getKey().toString());
					}
	    		}
	    	}
		}
		return list;


	}

	@Override
	public boolean testPermission(CommandSource source) {
		Optional<String> permission = getHelp().getPermission();
		
		if(permission.isPresent()) {
			return source.hasPermission(permission.get());
		}
		return true;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.of(Text.of(getHelp().getDescription()));
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.of(Text.of(getHelp().getDescription()));
	}

	@Override
	public Text getUsage(CommandSource source) {
		return getHelp().getUsageText();
	}
	
	public Help getHelp() {
		return help;
	}
}
