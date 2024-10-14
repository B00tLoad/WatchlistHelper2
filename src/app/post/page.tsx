import {Button} from "~/components/ui/button";
import Link from "next/link";

export default async function Post() {
  return (
        <div className="grid grid-cols-2 gap-4 p-6 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
          <div className="group relative overflow-hidden rounded-lg">
            <h1 className={"text-gray-100"}>Post 1</h1>
          </div>
          <div className="group relative overflow-hidden rounded-lg">

            <Link href="/">
              <Button>
                Home
              </Button>
            </Link>
          </div>
        </div>
  );
}
