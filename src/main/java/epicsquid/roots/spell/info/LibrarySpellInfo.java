package epicsquid.roots.spell.info;

import epicsquid.mysticallib.util.ItemUtil;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.modifiers.instance.library.LibraryModifierInstanceList;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.info.storage.LibrarySpellStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;

public class LibrarySpellInfo extends AbstractSpellModifiers<LibraryModifierInstanceList> {
  public static LibrarySpellInfo EMPTY = new LibrarySpellInfo();

  private boolean obtained;

  private LibrarySpellInfo() {
  }

  public LibrarySpellInfo(SpellBase spell) {
    super(spell);
    this.modifiers = new LibraryModifierInstanceList(spell);
    this.obtained = false;
  }

  @Override
  public LibraryModifierInstanceList getModifiers() {
    return modifiers;
  }

  @Override
  public void setModifiers(LibraryModifierInstanceList libraryModifierInstances) {
    this.modifiers = libraryModifierInstances;
  }

  public boolean isObtained() {
    return obtained;
  }

  public void setObtained() {
    setObtained(true);
  }

  public void setObtained(boolean value) {
    this.obtained = value;
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT result = super.serializeNBT();
    result.put("m", modifiers.serializeNBT());
    result.putBoolean("o", obtained);
    return result;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    super.deserializeNBT(nbt);
    SpellBase spell = getSpell();
    if (spell != null) {
      this.modifiers = new LibraryModifierInstanceList(spell);
      this.modifiers.deserializeNBT(nbt.getCompound("m"));
      this.obtained = nbt.getBoolean("o");
    }
  }

  @Override
  public boolean isEmpty() {
    return this == EMPTY;
  }

  @Override
  public ItemStack asStack() {
    ItemStack stack = new ItemStack(ModItems.spell_icon);
    CompoundNBT comp = ItemUtil.getOrCreateTag(stack);
    comp.putBoolean("library", true);
    LibrarySpellStorage storage = LibrarySpellStorage.fromStack(stack);
    Objects.requireNonNull(storage).addSpell(this);
    return stack;
  }

  public StaffSpellInfo toStaff() {
    SpellBase spell = getSpell();
    if (spell != null) {
      StaffSpellInfo info = new StaffSpellInfo(spell);
      info.setModifiers(modifiers.toStaff());
      return info;
    } else {
      return StaffSpellInfo.EMPTY;
    }
  }

  public static LibrarySpellInfo fromNBT(CompoundNBT tag) {
    LibrarySpellInfo instance = new LibrarySpellInfo();
    instance.deserializeNBT(tag);
    return instance;
  }

  public static LibrarySpellInfo fromStaff(StaffSpellInfo incoming) {
    SpellBase spell = incoming.getSpell();
    if (spell != null) {
      LibrarySpellInfo instance = new LibrarySpellInfo(spell);
      instance.setObtained(true);
      instance.setModifiers(incoming.getModifiers().toLibrary());
      return instance;
    } else {
      return LibrarySpellInfo.EMPTY;
    }
  }
}
