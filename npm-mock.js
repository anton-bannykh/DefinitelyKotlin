var hyperquest = require('hyperquest'),
        concat = require('concat-stream');

var registryMock = require('registry-mock');

var fs = require('fs');

var port = process.env.PORT || 3676;

registryMock({ http: port }, function (err, registry) {
  if (err) { throw err; }

  console.log('npm-registry-echo listening on %s', port);

  registry.server.on('request', function (req) {
    if (req.headers['x-fetch-cache']) { return; }

    var status;

    hyperquest('http://localhost:' + port + req.url, {
      headers: {
        'x-fetch-cache': true,
        'x-clear-cache': true
      }
    }).pipe(concat({ encoding: 'string' }, function (data) {
      console.log('%s %s', req.method, req.url);

      fs.writeFile('data.json', data, function(err) {
        if (err) {
            console.log(err);
            process.exit(1);
        }
        console.log("data.json written successfully");
        process.exit(0);
      })
    }));
  });
});
