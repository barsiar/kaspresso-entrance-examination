package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CerealStorageImplTest {

    @Test
    fun `should throw Exception if containerCapacity is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(-4f, 10f)
        }
    }

    @Test
    fun `should throw Exception if positive but less than storageCapacity is less than containerCapacity`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(15f, 10f)
        }
    }

    @Test
    fun `should initialize if containerCapacity is positive and less than storageCapacity`() {
        val storage = CerealStorageImpl(10f, 30f)
        assertEquals(10f, storage.containerCapacity)
        assertEquals(30f, storage.storageCapacity)
    }

    @Test
    fun `should allow storageCapacity equal to containerCapacity`() {
        val storage = CerealStorageImpl(10f, 10f)
        assertEquals(10f, storage.containerCapacity)
        assertEquals(10f, storage.storageCapacity)
    }

    @Test
    fun `should throw exception when amount is negative`() {
        val storage = CerealStorageImpl(10f, 30f)
        assertThrows(IllegalArgumentException::class.java) {
            storage.getCereal(Cereal.RICE, -5f)
        }
    }

    @Test
    fun `should return entire amount as leftover when cereal does not exist`() {
        val storage = CerealStorageImpl(10f, 30f)
        assertEquals(5f, storage.getCereal(Cereal.PEAS, 5f))
    }

    @Test
    fun `should reduce amount in container and return leftover`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BUCKWHEAT, 10f)
        assertEquals(0f, storage.getCereal(Cereal.BUCKWHEAT, 7f))
        assertEquals(3f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `should return leftover when requested amount exceeds stock`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BULGUR, 5f)
        assertEquals(2f, storage.getCereal(Cereal.BULGUR, 7f))
        assertEquals(0f, storage.getAmount(Cereal.BULGUR))
    }

    @Test
    fun `should remove cereal container when stock reaches zero`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.RICE, 5f)
        assertEquals(0f, storage.getCereal(Cereal.RICE, 5f))
        assertEquals(5f, storage.getCereal(Cereal.RICE, 5f))
    }

    @Test
    fun `should handle multiple cereals independently`() {
        val storage = CerealStorageImpl(10f, 30f)

        // Добавляем 10 единиц риса (в контейнер помещается полностью)
        assertEquals(0f, storage.addCereal(Cereal.RICE, 10f))
        // Добавляем 15 единиц гречки (помещается только 10, остаток — 5)
        assertEquals(5f, storage.addCereal(Cereal.BUCKWHEAT, 15f))
        // Забираем 5 единиц риса
        assertEquals(0f, storage.getCereal(Cereal.RICE, 5f))
        // Проверяем остаток риса
        assertEquals(5f, storage.getAmount(Cereal.RICE))
        // Проверяем, что в контейнер с гречкой помещается лишь 10 единиц (остаток уже учтён)
        assertEquals(10f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `should return true when container does not exist`() {
        val storage = CerealStorageImpl(10f, 30f)
        assertTrue(storage.removeContainer(Cereal.RICE))
    }

    @Test
    fun `should return true and remove container when it is empty`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BULGUR, 10f)
        storage.getCereal(Cereal.BULGUR, 10f)
        assertTrue(storage.removeContainer(Cereal.BULGUR))
        assertEquals(5f, storage.getCereal(Cereal.BULGUR, 5f))
    }

    @Test
    fun `should return false when container is not empty`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertFalse(storage.removeContainer(Cereal.BUCKWHEAT))
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `should return 0 when container does not exist`() {
        val storage = CerealStorageImpl(10f, 30f)
        assertEquals(0f, storage.getAmount(Cereal.RICE))
    }

    @Test
    fun `should return the correct amount when container exists`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BUCKWHEAT, 10f)
        assertEquals(10f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `should return updated amount after operations`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BULGUR, 7f)
        storage.getCereal(Cereal.BULGUR, 2f)
        assertEquals(5f, storage.getAmount(Cereal.BULGUR))
    }

    @Test
    fun `should return full container capacity when container does not exist`() {
        val storage = CerealStorageImpl(10f, 30f)
        assertEquals(10f, storage.getSpace(Cereal.RICE))
    }

    @Test
    fun `should return correct space for partially filled container`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BUCKWHEAT, 3f)
        assertEquals(7f, storage.getSpace(Cereal.BUCKWHEAT))
    }

    @Test
    fun `should return zero when container is full`() {
        val storage = CerealStorageImpl(10f, 30f)
        storage.addCereal(Cereal.BULGUR, 10f)
        assertEquals(0f, storage.getSpace(Cereal.BULGUR))
    }
}