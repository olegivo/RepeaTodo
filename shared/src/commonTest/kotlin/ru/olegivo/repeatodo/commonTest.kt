package ru.olegivo.repeatodo

import io.kotest.core.spec.style.FreeSpec
import kotlin.test.assertContains

class GreetingTest : FreeSpec({
    "should contains Hello" {
        assertContains(Greeting().greeting(), "Hello", message = "Check 'Hello' is mentioned")
    }
})