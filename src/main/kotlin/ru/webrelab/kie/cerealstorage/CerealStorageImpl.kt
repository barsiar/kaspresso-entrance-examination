package ru.webrelab.kie.cerealstorage

class CerealStorageImpl(
    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {

    /**
     * Блок инициализации класса.
     * Выполняется сразу при создании объекта
     */
    init {
        require(containerCapacity >= 0) {
            "Ёмкость контейнера не может быть отрицательной: $containerCapacity"
        }
        require(storageCapacity >= containerCapacity) {
            "Ёмкость хранилища не должна быть меньше ёмкости одного контейнера: $storageCapacity < $containerCapacity"
        }
    }

    private val storage = mutableMapOf<Cereal, Float>()

    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) {
            "Количество добавляемой крупы не может быть отрицательным: $amount"
        }

        val currentAmount = storage[cereal] ?: 0f // Текущее количество в контейнере, если он есть
        val availableSpace = containerCapacity - currentAmount // Свободное место в контейнере

        if (availableSpace >= amount) {
            // Если всё добавляемое количество помещается, обновляем хранилище
            storage[cereal] = currentAmount + amount
            return 0f // Остатка нет
        }

        // Если не хватает места в текущем контейнере
        storage[cereal] = containerCapacity
        val leftover = amount - availableSpace // Подсчитываем оставшееся количество

        // Проверяем, можно ли создать новый контейнер для оставшегося количества
        if (storage.size * containerCapacity + leftover > storageCapacity) {
            throw IllegalStateException("Лимит хранилища достигнут. Невозможно добавить больше крупы.")
        }

        return leftover // Возвращаем остаток, который не влез
    }

    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) { "Количество не может быть отрицательным: $amount" }

        val currentAmount = getAmount(cereal) // Получаем текущий объем

        if (currentAmount == 0f) {
            return amount // Контейнера нет -> возвращаем весь запрошенный объем
        }

        return if (currentAmount >= amount) {
            // Хватает: уменьшаем объем в контейнере
            storage[cereal] = currentAmount - amount
            if (storage[cereal] == 0f) {
                storage.remove(cereal) // Удаляем контейнер, если пуст
            }
            0f // Всё запрошенное успешно извлечено
        } else {
            // Не хватает: выдаём всё, что есть
            storage.remove(cereal) // Контейнер опустел, удаляем его
            amount - currentAmount // Возвращаем остаток
        }
    }

    override fun removeContainer(cereal: Cereal): Boolean {
        val currentAmount = storage[cereal] ?: return true // Контейнер не существует => уже удалён

        return if (currentAmount == 0f) {
            storage.remove(cereal) // Контейнер пуст, удаляем
            true
        } else {
            false // Контейнер содержит крупу, удаление невозможно
        }
    }

    override fun getAmount(cereal: Cereal): Float {
        return storage[cereal] ?: 0f // Возвращаем объем или 0, если контейнера нет
    }

    override fun getSpace(cereal: Cereal): Float {
        val currentAmount = storage[cereal] ?: 0f // Получаем текущее количество крупы (0, если контейнер отсутствует)
        return containerCapacity - currentAmount // Вычисляем оставшееся место
    }

    override fun toString(): String {
        return "containerCapacity = $containerCapacity, storageCapacity = $storageCapacity"
    }
}
