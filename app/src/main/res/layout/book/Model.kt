package com.badr.book

class Model {
    var Image :String?=null
    constructor(  ):this(""){

    }

    constructor( Image :String?)
    {
        this.Image = Image
    }
}