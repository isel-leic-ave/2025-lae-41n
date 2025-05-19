

function getValuesEager() {
    var res = []
    console.log("Adding 1")
    res.push(1)
    console.log("Adding 2")
    res.push(2)
    console.log("Adding 3")
    res.push(3)
    console.log("Adding 4")
    res.push(4)
    console.log("Adding 5")
    res.push(5)
    return res
}

function* getValuesLazy() {
    yield 1
    console.log("*Adding 1")
    yield 2
    console.log("*Adding 2")
    yield 3
    console.log("*Adding 3")
    yield 4
    console.log("*Adding 4")
    yield 5
    console.log("*Adding 5")
}

function* getValuesLazyWithLoop() {
    for(let i = 0; i < 6; ++i) {
        yield i
        console.log(`*Adding ${i+1}`)
    }
}

function showValues(iter, numElemsToShow = -1) {
    for (const i of iter) {
        if(numElemsToShow != -1 && numElemsToShow-- == 0)
            break
        console.log(i)
    }
}

const eagerValues = getValuesEager()
const lazyValues = getValuesLazy()
const lazyValuesLoop = getValuesLazyWithLoop()

console.log("Eager values")
showValues(eagerValues, 2)
console.log("*Lazy values")
showValues(lazyValues, 2)
console.log("*Lazy values loop")
showValues(lazyValuesLoop, 2)


