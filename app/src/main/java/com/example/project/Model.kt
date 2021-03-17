package com.example.project

class Model {
    var Image :String?=null
    constructor(  ):this(""){

    }

    constructor( Image :String?)
    {
        this.Image = Image
    }
}