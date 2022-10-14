import java.lang.NumberFormatException

/**
 * Сложная (6 баллов)
 *
 * Строка содержит названия товаров и цены на них в формате вида
 * "Хлеб 39.9; Молоко 62; Курица 184.0; Конфеты 89.9".
 * То есть, название товара отделено от цены пробелом,
 * а цена отделена от названия следующего товара точкой с запятой и пробелом.
 * Вернуть название самого дорогого товара в списке (в примере это Курица),
 * или пустую строку при нарушении формата строки.
 * Все цены должны быть больше нуля либо равны нулю.
 */

class HomeWork8 (productPrices:String) {
    private var products:List<String>

    init {
        products = productPrices.split("; ")
    }

    public fun getOut(getMax:Boolean = true) {
        var maxPrice = 0.0
        var veryExpensiveProduct = ""
        var minPrice = Double.MAX_VALUE
        var veryCheapProduct = ""
        for (product in this.products) {
            var rowList = product.split(" ")
            try {
                rowList[1].toDouble()
            } catch (e:IndexOutOfBoundsException) {
                println("Ошибка при парсинге значений в строке")
            } catch (e:NumberFormatException) {
                println("Стоимость указа не в Double")
            }
            if (rowList[1].toDouble() > maxPrice) {
                maxPrice = rowList[1].toDouble()
                veryExpensiveProduct = rowList[0]
            }
            if (rowList[1].toDouble() < minPrice) {
                minPrice = rowList[1].toDouble()
                veryCheapProduct = rowList[0]
            }
        }
        if (getMax) {
            println("Самый дорогой продукт в списке это ${veryExpensiveProduct}, который стоит ${maxPrice} денежных единиц!" )
        } else {
            println("Самый дешевый продукт в списке это ${veryCheapProduct}, который стоит ${minPrice} денежных единиц!" )
        }
    }
}

fun main(args: Array<String>) {
    var calc = HomeWork8("Хлеб 123; Молоко 62.0; Курица 184.0; Мясо 254.50; Конфеты 89.9; Яйца 110.0")
    calc.getOut()
    calc.getOut(false)
}

