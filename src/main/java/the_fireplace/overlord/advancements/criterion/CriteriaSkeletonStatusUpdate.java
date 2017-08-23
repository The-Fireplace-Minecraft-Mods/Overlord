package the_fireplace.overlord.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import the_fireplace.overlord.Overlord;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CriteriaSkeletonStatusUpdate implements ICriterionTrigger<CriteriaSkeletonStatusUpdate.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(Overlord.MODID, "status_update");
	private final Map<PlayerAdvancements, CriteriaSkeletonStatusUpdate.Listeners> listeners = Maps.newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<CriteriaSkeletonStatusUpdate.Instance> listener) {
		CriteriaSkeletonStatusUpdate.Listeners milkDrinkListeners = this.listeners.get(playerAdvancementsIn);

		if (milkDrinkListeners == null) {
			milkDrinkListeners = new CriteriaSkeletonStatusUpdate.Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, milkDrinkListeners);
		}

		milkDrinkListeners.add(listener);
	}

	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<CriteriaSkeletonStatusUpdate.Instance> listener) {
		CriteriaSkeletonStatusUpdate.Listeners milkDrinkListeners = this.listeners.get(playerAdvancementsIn);

		if (milkDrinkListeners != null) {
			milkDrinkListeners.remove(listener);

			if (milkDrinkListeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public CriteriaSkeletonStatusUpdate.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(json.get("item"));
		EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("entity"));
		return new CriteriaSkeletonStatusUpdate.Instance(itemPredicate, entitypredicate);
	}

	public void trigger(EntityPlayerMP player, Entity entityIn, Item criteria, int value) {
		CriteriaSkeletonStatusUpdate.Listeners playerhurtentitytrigger$listeners = this.listeners.get(player.getAdvancements());

		if (playerhurtentitytrigger$listeners != null) {
			playerhurtentitytrigger$listeners.trigger(player, entityIn, criteria, value);
		}
	}

	public static class Instance extends AbstractCriterionInstance {
		private final ItemPredicate item;
		private final EntityPredicate entity;

		public Instance(ItemPredicate item, EntityPredicate entity) {
			super(ID);
			this.item = item;
			this.entity = entity;
		}

		public boolean test(EntityPlayerMP player, Entity entity, Item criteria, int value) {
			if (!this.item.test(new ItemStack(criteria, 1, value))) {
				return false;
			} else {
				return this.entity.test(player, entity);
			}
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<Listener<CriteriaSkeletonStatusUpdate.Instance>> listeners = Sets.newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<CriteriaSkeletonStatusUpdate.Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<CriteriaSkeletonStatusUpdate.Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(EntityPlayerMP player, Entity entity, Item criteria, int value) {
			List<Listener<CriteriaSkeletonStatusUpdate.Instance>> list = null;

			for (ICriterionTrigger.Listener<CriteriaSkeletonStatusUpdate.Instance> listener : this.listeners) {
				if ((listener.getCriterionInstance()).test(player, entity, criteria, value)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (ICriterionTrigger.Listener<CriteriaSkeletonStatusUpdate.Instance> listener1 : list) {
					listener1.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
}
