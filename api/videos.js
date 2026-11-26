function send(res,status,payload){
  res.statusCode=status;
  res.setHeader("Content-Type","application/json; charset=utf-8");
  res.setHeader("Cache-Control","no-store, no-cache, must-revalidate, proxy-revalidate");
  res.end(JSON.stringify(payload));
}
export default async function handler(req,res){
  if(req.method!=="GET"){res.setHeader("Allow","GET");return send(res,405,{error:"Method not allowed."});}
  const topic=typeof req.query.topic==="string"?req.query.topic.trim().slice(0,180):"";
  const part=typeof req.query.part==="string"?req.query.part.trim().slice(0,80):"Organizational Management";
  if(!topic)return send(res,400,{error:"A syllabus topic is required."});
  const key=process.env.YOUTUBE_API_KEY;
  if(!key)return send(res,503,{error:"Automatic video search is ready, but YOUTUBE_API_KEY is not configured in Vercel."});
  try{
    const q=topic+" "+part+" lecture tutorial";
    const url=new URL("https://www.googleapis.com/youtube/v3/search");
    url.search=new URLSearchParams({part:"snippet",type:"video",q:q,maxResults:"8",order:"relevance",relevanceLanguage:"en",safeSearch:"strict",videoEmbeddable:"true",key:key}).toString();
    const response=await fetch(url.toString(),{cache:"no-store"});
    const data=await response.json().catch(()=>({}));
    if(!response.ok)return send(res,response.status===403?503:502,{error:data.error&&data.error.message?data.error.message:"YouTube search failed. Check API access and quota."});
    const items=(data.items||[]).filter(x=>x.id&&x.id.videoId&&x.snippet).map(x=>({
      videoId:x.id.videoId,
      title:x.snippet.title||"YouTube lesson",
      channelTitle:x.snippet.channelTitle||"YouTube",
      thumbnail:x.snippet.thumbnails&&((x.snippet.thumbnails.medium||x.snippet.thumbnails.high||x.snippet.thumbnails.default)||{}).url||""
    }));
    return send(res,200,{items:items,topic:topic});
  }catch(error){return send(res,500,{error:"Video search service error. Please try again."});}
}
