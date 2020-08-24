package dev.faizaan.cornelltours

import net.minecraft.server.v1_16_R1.*
import java.lang.reflect.Field
import java.util.*

/**
 * Provides utilities for dealing with reflection and NMS.
 */
object NmsUtils {
    /**
     * Clears all AI goals from an [EntityInsentient].
     */
    fun clearGoals(nmsEntity: EntityInsentient) {
        val goalSelector = nmsEntity.goalSelector
        val targetSelector = nmsEntity.targetSelector
        try {
            val brField = EntityLiving::class.java.getDeclaredField("bn")
            brField.isAccessible = true
            val controller = brField[nmsEntity] as BehaviorController<*>
            val memoriesField = BehaviorController::class.java.getDeclaredField("memories")
            memoriesField.isAccessible = true
            memoriesField[controller] = HashMap<Any, Any>()
            val sensorsField = BehaviorController::class.java.getDeclaredField("sensors")
            sensorsField.isAccessible = true
            sensorsField[controller] = LinkedHashMap<Any, Any>()
            val cField = BehaviorController::class.java.getDeclaredField("e")
            cField.isAccessible = true
            cField[controller] = TreeMap<Any, Any>()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        try {
            val dField: Field = PathfinderGoalSelector::class.java.getDeclaredField("d")
            dField.isAccessible = true
            dField[goalSelector] = LinkedHashSet<Any>()
            dField[targetSelector] = LinkedHashSet<Any>()
            val cField: Field = PathfinderGoalSelector::class.java.getDeclaredField("c")
            cField.isAccessible = true
            dField[goalSelector] = LinkedHashSet<Any>()
            cField[targetSelector] = EnumMap<PathfinderGoal.Type, Any>(PathfinderGoal.Type::class.java)
            val fField: Field = PathfinderGoalSelector::class.java.getDeclaredField("f")
            fField.isAccessible = true
            dField[goalSelector] = LinkedHashSet<Any>()
            fField[targetSelector] = EnumSet.noneOf(PathfinderGoal.Type::class.java)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}