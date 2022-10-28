import java.lang.NumberFormatException
import kotlin.system.exitProcess

class Sapper(matrixSize:Int, maxMinesCount:Int) {

    private val mineDigit:Int = 10
    private val emptyDigit:Int = 0
    private val mineSymbol = "\uD83E\uDDE8"
    private val emptySymbol = "   "
    private val closedSymbol = " \uD800\uDD02 "

    private var mineCount:Int = 0
    private var matrixSize:Int = 0
    private var minesSet = mutableSetOf<String>() //все установленные на поле мины
    private var openCellsSet = mutableSetOf<String>() //все уже открытые на поле ячейки
    private var gameMatrix = arrayOf<Array<Int>>()

    init {
        this.matrixSize = matrixSize
        this.mineCount = maxMinesCount
        this.gameMatrix = setDefaultMatrix()
        mineGameField()
    }

    private fun setDefaultMatrix(): Array<Array<Int>> {
        var matrix = arrayOf<Array<Int>>()
        for (i in 0..this.matrixSize-1) {
            var row = arrayOf<Int>()
            for (j in 0..this.matrixSize-1) {
                row += this.emptyDigit
            }
            matrix += row
        }
        return matrix
    }

    //печать игрового поля (закрытого по дефолту)
    public fun printMatrix(printAsOpen:Boolean = false) {
        //печать номеров столбцов
        print ("  ")
        for (i in 0..this.matrixSize-1) {
            print(" ${i} ")
        }
        println("")
        println("  _____________________________")

        //печать номеров строк и самих строк
        for (i in 0..this.matrixSize-1) {
            print ("${i}|")
            for (j in 0..this.matrixSize-1) {
                if (printAsOpen) {
                    if (this.gameMatrix[i][j] == this.mineDigit) {
                        print(this.mineSymbol)
                    } else print(" ${this.gameMatrix[i][j]} ")
                } else {
                    if (this.openCellsSet.contains(i.toString()+";"+j.toString())) {
                        if (this.gameMatrix[i][j] == this.emptyDigit ) {
                            print(this.emptySymbol)
                        } else  print(" ${this.gameMatrix[i][j]} ")
                    } else {
                        print(this.closedSymbol)
                    }
                }
            }
            println()
        }
    }

    //ставим мину (ее цифровове обозначение) в указанные координаты и заполняем ячейки вокруг текущей кол-вом бобм рядом
    private fun placeMine(i:Int, j:Int) {
        this.gameMatrix[i][j] = this.mineDigit
        //координаты смежных ячеек вокруг заданной клетки
        val cells = mapOf(
            0 to (i+1).toString()+";"+j.toString(), 1 to (i-1).toString()+";"+j.toString(), 2 to i.toString()+";"+(j+1).toString(),
            3 to i.toString()+";"+(j-1).toString(), 4 to (i-1).toString()+";"+(j+1).toString(), 5 to (i+1).toString()+";"+(j+1).toString(),
            6 to (i-1).toString()+";"+(j-1).toString(), 7 to (i+1).toString()+";"+(j-1).toString()
        )

        for ((index, cell) in cells) {
            try {
                var coords = cell.toString().split(";").toTypedArray()
                var cellValue = this.gameMatrix[coords[0].toInt()][coords[1].toInt()]
                if (cellValue != this.mineDigit) {
                    this.gameMatrix[coords[0].toInt()][coords[1].toInt()] += 1
                }
            } catch (e:IndexOutOfBoundsException) {null} //игнорим ошибку, если вышли за пределы поля
        }
    }

    //первичная случайная расстановка мин по полю
    private fun mineGameField() {
        while (this.minesSet.size < this.mineCount) {
            var coords = (0..this.matrixSize-1).random().toString()+";"+(0..this.matrixSize-1).random().toString()
            this.minesSet.add(coords)
        }
        //ставим бомбы по полю
        for (bomb in this.minesSet) {
            var coords = bomb.toString().split(";").toTypedArray()
            this.placeMine(coords[0].toInt(), coords[1].toInt())
        }
    }

    //проверка на выход индексов из матрицы
    private fun isCellValid(i:Int, j:Int):Boolean {
        try {
            var value:Int = this.gameMatrix[i][j]
            return true
        } catch (e: IndexOutOfBoundsException) {
            return false
        }
    }

    private fun getCellValue(i:Int,j:Int):Int {
        return this.gameMatrix[i][j];
    }

    public fun isCellOpen(i:Int,j:Int):Boolean {
        return this.openCellsSet.contains(i.toString() + ";" + j.toString())
    }

    public fun isCellMined(i:Int,j:Int):Boolean {
        return this.minesSet.contains(i.toString() + ";" + j.toString())
    }

    public fun isGameWin(): Boolean {
        return ((this.matrixSize * this.matrixSize) - this.openCellsSet.size) == this.minesSet.size

    }

    public fun openCellsArround(i:Int,j:Int) {
        if (this.isCellValid(i,j) && !this.openCellsSet.contains(i.toString() + ";" + j.toString())) {
            if (getCellValue(i, j) == this.emptyDigit) { //надо открыть эту ячейку и обойти соседей
                this.openCellsSet.add(i.toString() + ";" + j.toString())
                //рекурсивно обходим соседей 8 штук и их соседей
                openCellsArround(i, j + 1)
                openCellsArround(i, j - 1)
                openCellsArround(i + 1, j)
                openCellsArround(i - 1, j)
                openCellsArround(i - 1, j + 1)
                openCellsArround(i + 1, j + 1)
                openCellsArround(i - 1, j - 1)
                openCellsArround(i + 1, j - 1)
            } else if (getCellValue(i, j) < this.mineDigit){
                //если в ячейке цифра, но не мина) просто открываем
                this.openCellsSet.add(i.toString() + ";" + j.toString())
            }
        }
    }

}


fun userInput(): String {
    return readln()
}

fun play(sapper:Sapper){
    var coords:Array<String>
    var i:Int = -1
    var j:Int = -1
    do {
        println()
        println("Ходите. Пробел разделяет координаты. Например 0 2")
        coords = userInput().toString().split(" ").toTypedArray()
        try {
            i = coords[0].toInt()
            j = coords[1].toInt()
        } catch (e:IndexOutOfBoundsException) {
            println("Ошибка ввода! Вводите координаты правильно!")
        } catch (e:NumberFormatException) {
            println("Ошибка ввода! Вводите координаты правильно!")
        }
    } while(!isMoveValid(i,j, sapper));
    sapper.openCellsArround(i,j)
    sapper.printMatrix()
}

fun isMoveValid(i:Int, j:Int, sapper: Sapper):Boolean {
    if (sapper.isCellOpen(i,j)) {     //если ячейка уже открыта
        println("Ячейка уже окрыта. Давайте еще")
        return false
    } else if (sapper.isCellMined(i,j)) { //если в ячейке мина
        println("В ячейке мина, вы проиграли(")
        sapper.printMatrix(true)
        exitProcess(0)
    } else {
        return true
    }
}

fun main(args: Array<String>) {
    var sapper = Sapper(10,10)
    sapper.printMatrix()

    while (true) {
        if (sapper.isGameWin()) {
            println("Вы выиграли")
            exitProcess(0)
        }
        play(sapper)
    }
}
