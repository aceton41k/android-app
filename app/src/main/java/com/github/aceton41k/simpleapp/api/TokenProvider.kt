package com.github.aceton41k.simpleapp.api

interface TokenProvider {
    fun getToken(): String?
}