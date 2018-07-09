package ssyp18
import java.awt.MouseInfo


fun main(args : Array<String>) {
    val location = MouseInfo.getPointerInfo().getLocation()
    val x = location.getX ()
    val y = location.getY ()

    System.out.println("x = " + x)
    System.out.println("y = " + y)
}