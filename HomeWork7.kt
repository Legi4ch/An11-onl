fun main(args: Array<String>) {


    val romMap = mapOf(1000 to "M", 900 to "CM", 500 to "D", 400 to "CD", 100 to "C", 90 to "XC",
        50 to "L", 40 to "XL", 10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I")


    var roman:String = ""
    var value = 4022


    while(value > 0) {
        for ((digit, letter) in romMap) {
            while (value >= digit) {
                roman += letter
                value -= digit
            }
        }
    }
    println("Римская запись ${value} это ${roman}")
}




