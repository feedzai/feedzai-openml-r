loadModel <- function() {
    return(NULL)
}

getClassDistribution <- function(instance) {
    set.seed(instance[1,1])
    aDist = runif(1)
    return(data.frame(a = aDist, b = 1 - aDist))
}

classify <- function(instance) {
    return('b')
}
