# URL Shortener Service
<b>Requirements:</b>
* Expose a route for URL shortener generation (they must be unique)
* Expose a route to retrieve the real URL via given shortened URL (we do not redirect)
* Based on each retrieval, increase click ratio (hit rate) and store it
* Expose a route to read hit rates for each shortened URL


<b>Good to have:</b>
* Follow REST API conventions (It means a lot to us!)
* Use cache layer in case applicable
* Human friendly URLs based on given URL (if you have a solution for)
* You can use Bijective Function


<b>Note:</b>
* Shortened URL cannot be more than 6 characters in path: goo.gl/jqdm05
* Redirection is the essence of URL shorteners. BUT in our sample API we just need to see how you generate your shortened URLs and how you handle click-ratio/hit-rate.
* This project is a monolithic application, so do NOT use micro-service architecture.

<sub>This is a WIP.</sub>
