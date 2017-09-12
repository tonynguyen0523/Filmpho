# Filmpho
Built an app, optimized for tablets, to help users discover popular and highly rated movies on the web. It displays a scrolling grid of movie trailers, launches a details screen whenever a particular movie is selected, allows users to save favorites, play trailers, and read user reviews. This app utilizes core Android user interface components and fetches movie information using themoviedb.org web API.

### Preview
![home gif](https://media.giphy.com/media/11F2PIUsDJcGIw/giphy.gif)
![detail gif](https://media.giphy.com/media/Rr3zlRrURh5ss/giphy.gif)
![fav gif](https://media.giphy.com/media/zW9KLdYgZCPxm/giphy.gif)

## Getting Started

### Installation
Clone the GitHub repository and use Bundler to install the gem dependencies.
```
$ git clone https://github.com/tonynguyen0523/Filmpho.git
$ cd Filmpho
$ bundle install
```
### Required
[API key][1] required.
Insert your API key into ``` app/build.gradle ```
```gradle
   buildTypes.each {
        it.buildConfigField('String', 'OPEN_MOVIE_API_KEY', "\"your-api-key\"")
    }
```


[1]: https://www.themoviedb.org/documentation/api
