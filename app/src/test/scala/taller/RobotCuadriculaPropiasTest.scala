package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RobotCuadriculaPropiasTest extends AnyFunSuite {

  val r = new RobotCuadricula()
  import r._

  val cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))

  // Punto 2 -------------------------------------------------------------------

  test("ejecutar propia: avanzar al este aumenta x") {
    assert(ejecutar(Estado(2, 3, Este), Avanzar(4)) == Estado(6, 3, Este))
  }

  test("ejecutar propia: avanzar al sur desde el origen da y negativa") {
    assert(ejecutar(Estado(0, 0, Sur), Avanzar(3)) == Estado(0, -3, Sur))
  }

  test("ejecutar propia: repetir tres veces avanzar dos") {
    assert(ejecutar(inicial, Repetir(3, Avanzar(2))) == Estado(0, 6, Norte))
  }

  test("ejecutar propia: repetir un número negativo de veces no hace nada") {
    assert(ejecutar(inicial, Repetir(-2, Avanzar(5))) == inicial)
  }

  test("ejecutar propia: girar a la izquierda y avanzar va al oeste") {
    assert(ejecutar(inicial, Secuencia(List(Girar(Izquierda), Avanzar(3)))) == Estado(-3, 0, Oeste))
  }

  test("ejecutar propia: dos vueltas de avanzar y girar a la derecha terminan mirando al sur") {
    val o = Repetir(2, Secuencia(List(Avanzar(1), Girar(Derecha))))
    assert(ejecutar(inicial, o) == Estado(1, 1, Sur))
  }

  test("ejecutar propia: repeticiones anidadas multiplican los pasos") {
    assert(ejecutar(inicial, Repetir(2, Repetir(3, Avanzar(1)))) == Estado(0, 6, Norte))
  }

  // Punto 3: cuantasBasicas ---------------------------------------------------

  test("cuantasBasicas propia: un giro cuenta uno") {
    assert(cuantasBasicas(Girar(Izquierda)) == 1)
  }

  test("cuantasBasicas propia: repetir tres veces una básica cuenta tres") {
    assert(cuantasBasicas(Repetir(3, Avanzar(2))) == 3)
  }

  test("cuantasBasicas propia: una secuencia suma sus básicas") {
    assert(cuantasBasicas(Secuencia(List(Avanzar(1), Girar(Derecha), Avanzar(2)))) == 3)
  }

  test("cuantasBasicas propia: las repeticiones anidadas se multiplican") {
    assert(cuantasBasicas(Repetir(2, Repetir(3, Avanzar(1)))) == 6)
  }

  test("cuantasBasicas propia: repetir algo vacío no ejecuta nada") {
    assert(cuantasBasicas(Repetir(5, Secuencia(Nil))) == 0)
  }

  test("cuantasBasicas propia: repetir un número negativo de veces no ejecuta nada") {
    assert(cuantasBasicas(Repetir(-1, Avanzar(1))) == 0)
  }

  test("cuantasBasicas propia: el cuadrado más un avance suma nueve") {
    assert(cuantasBasicas(Secuencia(List(cuadrado, Avanzar(1)))) == 9)
  }

  // Punto 3: profundidad ------------------------------------------------------

  test("profundidad propia: un giro tiene profundidad 1") {
    assert(profundidad(Girar(Izquierda)) == 1)
  }

  test("profundidad propia: repetir una básica tiene profundidad 2") {
    assert(profundidad(Repetir(3, Avanzar(2))) == 2)
  }

  test("profundidad propia: dos repeticiones anidadas dan profundidad 3") {
    assert(profundidad(Repetir(2, Repetir(3, Avanzar(1)))) == 3)
  }

  test("profundidad propia: en una secuencia manda la rama más honda") {
    assert(profundidad(Secuencia(List(Avanzar(1), Repetir(2, Girar(Derecha))))) == 3)
  }

  test("profundidad propia: una secuencia que contiene una secuencia vacía tiene profundidad 2") {
    assert(profundidad(Secuencia(List(Secuencia(Nil)))) == 2)
  }

  test("profundidad propia: tres secuencias anidadas con una básica dan profundidad 4") {
    assert(profundidad(Secuencia(List(Secuencia(List(Secuencia(List(Avanzar(1)))))))) == 4)
  }
}
