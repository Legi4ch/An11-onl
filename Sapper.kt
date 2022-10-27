import kotlin.system.exitProcess

//создаем игровое поле по умолчанию заполненное нулями
fun setDefaultMatrix(size:Int): Array<Array<Int>> {
    var matrix = arrayOf<Array<Int>>()
    for (i in 0..size-1) {
        var row = arrayOf<Int>()
        for (j in 0..size-1) {
            when (i) {
                j -> row += 0
                size-j-1 -> row += 0
                else -> {row += 0}
            }
        }
        matrix += row
    }
    return matrix
}

//ставим бомбу (ее цифровове обозначение) в указанные координаты и заполняем ячейки вокруг текущей кол-вом бобм рядом
fun setBomb(bombValue:Int, i:Int, j:Int, matrix:Array<Array<Int>>) {
    matrix[i][j] = bombValue
    //координаты смежных ячеек вокруг заданной клетки
    val cells = mapOf(
        0 to (i+1).toString()+";"+j.toString(), 1 to (i-1).toString()+";"+j.toString(), 2 to i.toString()+";"+(j+1).toString(),
        3 to i.toString()+";"+(j-1).toString(), 4 to (i-1).toString()+";"+(j+1).toString(), 5 to (i+1).toString()+";"+(j+1).toString(),
        6 to (i-1).toString()+";"+(j-1).toString(), 7 to (i+1).toString()+";"+(j-1).toString()
        )

    for ((index, cell) in cells) {
        try {
            var coords = cell.toString().split(";").toTypedArray()
            var cellValue = matrix[coords[0].toInt()][coords[1].toInt()]
            if (cellValue != bombValue) {
                matrix[coords[0].toInt()][coords[1].toInt()] += 1
            }
        } catch (e:IndexOutOfBoundsException) {null} //ничего не делаем, если вышли за пределы поля
    }
}


fun isCellValid(i:Int, j:Int, matrix: Array<Array<Int>>):Boolean {
    try {
        var value:Int = matrix[i][j]
        return true
    } catch (e: IndexOutOfBoundsException) {
        return false
    }
}


fun getCellValue(i:Int,j:Int, matrix: Array<Array<Int>>):Int {
    return matrix[i][j];
}

//открываем клетки вокруг текущей
fun openCellsArround(i:Int,j:Int, matrix:Array<Array<Int>>, openCells: MutableSet<String>) {
    if (isCellValid(i, j, matrix) && !openCells.contains(i.toString() + ";" + j.toString())) {
        if (getCellValue(i, j, matrix) == 0) { //надо открыть эту ячейку и обойти соседей
            openCells.add(i.toString() + ";" + j.toString()) //открываем
            //обходим соседей 8 штук
            openCellsArround(i + 1, j, matrix, openCells)
            openCellsArround(i - 1, j, matrix, openCells)
            openCellsArround(i, j + 1, matrix, openCells)
            openCellsArround(i, j - 1, matrix, openCells)
            openCellsArround(i - 1, j + 1, matrix, openCells)
            openCellsArround(i + 1, j + 1, matrix, openCells)
            openCellsArround(i - 1, j - 1, matrix, openCells)
            openCellsArround(i + 1, j - 1, matrix, openCells)
        } else if (getCellValue(i, j, matrix) < 10){
            //если в ячейке цифра, но не бомба) просто открываем
            openCells.add(i.toString() + ";" + j.toString()) //открываем
        }
    }
}


//печать открытого поля в конце игры, если проиграли
fun printOpenMatrix(matrix: Array<Array<Int>>) {
    //печать номеров столбцов
    print ("  ")
    for (i in 0..matrix[0].size-1) {
        print(" ${i} ")
    }
    println("")
    println("  _____________________________")


    //печать номеров строк и самих строк
    for (i in 0..matrix[0].size-1) {
        print ("${i}|")
        for (j in 0..matrix[0].size-1) {
            if (matrix[i][j] == 10) {
                print("\uD83E\uDDE8")
            } else print(" ${matrix[i][j]} ")
        }
        println()
    }
}

//печать текущего игрового поля
fun printGameMatrix(matrix: Array<Array<Int>>, openCells: MutableSet<String>) {
    //печать номеров столбцов
    print ("  ")
    for (i in 0..matrix[0].size-1) {
        print(" ${i} ")
    }
    println("")
    println("  _____________________________")


    //печать номеров строк и самих строк
    for (i in 0..matrix[0].size-1) {
        print ("${i}|")
        for (j in 0..matrix[0].size-1) {
            if (openCells.contains(i.toString()+";"+j.toString())) {
                if (matrix[i][j] == 0 ) {
                    print("   ") //пустое поле вместо 0
                } else  print(" ${matrix[i][j]} ")
            } else {
                print(" X ")
            }
        }
        println()
    }
}


//заполняет множество бомбами и расставляет их по полю
fun fillBombsSet(bombsSet: MutableSet<String>, bombCount:Int, maxSizeValue:Int, bombDigit:Int, matrix: Array<Array<Int>>) {
    while (bombsSet.size < bombCount) {
        var coords = (0..maxSizeValue-1).random().toString()+";"+(0..maxSizeValue-1).random().toString()
        bombsSet.add(coords)
    }
    //ставим бомбы по полю
    for (bomb in bombsSet) {
        var coords = bomb.toString().split(";").toTypedArray()
        setBomb( bombDigit, coords[0].toInt(), coords[1].toInt(), matrix)
    }
}


fun isMoveValid(i:Int, j:Int, cellsSet: MutableSet<String>, bombsSet: MutableSet<String>, matrix: Array<Array<Int>>):Boolean {
    if (cellsSet.contains(i.toString()+";"+j.toString())) {     //если ячейка уже открыта
        println("Ячейка уже окрыта. Давайте еще")
        return false
    } else if (bombsSet.contains(i.toString()+";"+j.toString())) { //если в ячейке бомба
        println("В ячейке бомба, вы проиграли(")
        printOpenMatrix(matrix)
        exitProcess(0)
    } else {
        return true
    }
}

fun userInput(): String {
    return readln()
}

//ход игрока
fun play(cellsSet: MutableSet<String>, bombsSet: MutableSet<String>, matrix: Array<Array<Int>>){
    var coords:Array<String>
    do {
        println()
        println("Ходите. Пробел разделяет координаты. Например 0 2")
        coords = userInput().toString().split(" ").toTypedArray()
    } while(!isMoveValid(coords[0].toInt(),coords[1].toInt(), cellsSet, bombsSet, matrix));
    openCellsArround(coords[0].toInt(),coords[1].toInt(),matrix,cellsSet)
    printGameMatrix(matrix, cellsSet)
}


fun main(args: Array<String>) {
    val matrixSize:Int = 10
    val bombsCount = 10
    val bombCellVal:Int = 10 //значение в ячйке с бомбой
    var bombsSet = mutableSetOf<String>() //все установленные на поле бомбы
    var openCellsSet = mutableSetOf<String>() //все уже открытые на поле ячейки


    //запускаем игру
    var gameMatrix = setDefaultMatrix(matrixSize) //настройки поля
    fillBombsSet(bombsSet, bombsCount, matrixSize, bombCellVal, gameMatrix) //ставим бомбы
    printOpenMatrix(gameMatrix)
    printGameMatrix(gameMatrix, openCellsSet) //рисуем поле

    //главный цикл
    while (true) {

        //проверка на выигрыш
        if (matrixSize*matrixSize - openCellsSet.size == bombsSet.size) {
            println("Вы выиграли")
            exitProcess(0)
        }
        //запрашиваем ход от игрока
        play(openCellsSet, bombsSet, gameMatrix)
    }
}
