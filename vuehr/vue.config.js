let proxyObj = {};

proxyObj['/ws'] = {
    ws: true,
    target: "ws://localhost:8081"
};
//表示把拦截到的请求转发到localhost:8081上面去，websocket关闭
proxyObj['/'] = {
    ws: false,
    target: 'http://localhost:8081',
    changeOrigin: true,
    //表示拦截到的地址不去修改
    pathRewrite: {
        '^/': ''
    }
}
module.exports = {
    devServer: {
        host: 'localhost',
        port: 8080,
        proxy: proxyObj
    }
}