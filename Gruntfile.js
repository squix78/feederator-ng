module.exports = function(grunt) {

    // 1. All configuration goes here 
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        livereloadx: {
            dir: 'src/main/webapp/',
            proxy: 'http://localhost:8080/',
            preferLocal: true,
            liveCSS: true,
            liveImg: true,

        },
        gruntfile: {
          src: 'Gruntfile.js'
        },
        bower_concat: {
          all: {
            dest: 'src/main/webapp/user/dist/lib.js',
            bowerOptions: {
              relative: false
            },
            exclude: [
                      'font-awesome', 'overthrow'
            ]
          }
        },
        concat: {   
            dist: {

                src: [
                    'src/main/webapp/user/js/*.js'  // This specific file
                ],
                dest: 'src/main/webapp/user/dist/app.js'
            }
        },

        uglify: {
            build: {
                src: ['src/main/webapp/user/dist/app.js', 'src/main/webapp/user/dist/lib.js'],
                dest: 'src/main/webapp/user/dist/app.min.js'
            }
        },
        less: {
          development: {
            options: {
              compress: true,
              yuicompress: true,
              optimization: 2
            },
            files: {
              // target.css file: source.less file
              "src/main/webapp/user/dist/main.css": "src/main/webapp/user/css/main.less"
            }
          }
        },
        watch: {
            html: {
              files: ['src/main/webapp/user/index.html', 'src/main/webapp/user/partials/*.html'],
              options: {
                nospawn: true
              }
            },
            scripts: {
                files: ['src/main/webapp/user/js/*.js'],
                tasks: ['concat', 'uglify'],
                options: {
                    spawn: false,
                },
            }, 
            styles: {
              tasks: ['less'],
              files: ['src/main/webapp/user/css/*.less'],
              options: {
                nospawn: true
              }
            }
        }

    });

    // 3. Where we tell Grunt we plan to use this plug-in.
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-bower-concat');
    grunt.loadNpmTasks('livereloadx');
    

    // 4. Where we tell Grunt what to do when we type "grunt" into the terminal.
    grunt.registerTask('default', ['concat', 'bower_concat', 'uglify', 'less']);
    grunt.registerTask('server', ['concat', 'bower_concat', 'uglify', 'less', 'livereloadx', 'watch']);

};