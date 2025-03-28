function mapper(src, dest) {
    Object.keys(src).forEach(key => {
        if(dest[key] != undefined) {
            dest[key] = src[key]
        }
    })
}

const a = {
    a: "Sport",
    b: "Lisboa",
    c: "e",
    d: "Banfica"
}

const b = {
    a: 1,
    b: 2,
    c: 3,
    d: 4
}

console.log(b)
mapper(a, b)
console.log(b)