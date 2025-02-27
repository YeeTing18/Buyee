package com.example.buyee

data class Category(val id: String = "",
                    val name: String = "")
val categories = listOf(
    Category("Gadget", "categoryId1"),
    Category("Cosmetic", "categoryId2"),
    Category("Grocery", "categoryId3"),
    Category("Cloth", "categoryId4"),
    Category("Others", "categoryId5")
)